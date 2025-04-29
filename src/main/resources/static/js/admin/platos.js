document.addEventListener("DOMContentLoaded", () => {
  try {
    document
      .querySelectorAll('[data-bs-toggle="tooltip"]')
      .forEach((el) => new bootstrap.Tooltip(el, { container: "body" }));
  } catch (e) {
    console.error("Error tooltips:", e);
  }

  const disponibles = document.getElementById("alergenosDisponibles");
  const seleccionados = document.getElementById("alergenosSeleccionados");
  if (disponibles && seleccionados) {
    new Sortable(disponibles, { group: "alergenos", animation: 150 });
    new Sortable(seleccionados, {
      group: "alergenos",
      animation: 150,
      onAdd: actualizarHiddenInput,
      onRemove: actualizarHiddenInput,
      onSort: actualizarHiddenInput,
    });
  }

  const newForm = document.querySelector("#modalNuevoPlato form");
  if (newForm) {
    newForm.addEventListener("submit", (e) => {
      const invalido = newForm.querySelector(":invalid");
      if (invalido) {
        e.preventDefault();
        newForm
          .querySelectorAll(".is-invalid")
          .forEach((x) => x.classList.remove("is-invalid"));
        invalido.classList.add("is-invalid");
        invalido.focus();
        return;
      }
      actualizarHiddenInput();
    });

    document
      .getElementById("modalNuevoPlato")
      .addEventListener("hidden.bs.modal", () => {
        newForm.reset();
        newForm
          .querySelectorAll(".is-invalid")
          .forEach((x) => x.classList.remove("is-invalid"));
        document.getElementById("alergenosSeleccionados").innerHTML = "";
      });
  }

  function actualizarHiddenInput() {
    const ids = Array.from(
      document.getElementById("alergenosSeleccionados").children
    ).map((li) => li.dataset.id);
    document.getElementById("alergenosInput").value = ids.join(",");
  }

  document.querySelectorAll('[id^="seleccionados-"]').forEach((lista) => {
    const id = lista.id.split("-")[1];
    new Sortable(lista, {
      group: "alergenos-edit",
      animation: 150,
      onAdd: () => actualizarInput(id),
      onRemove: () => actualizarInput(id),
      onSort: () => actualizarInput(id),
    });
  });
  document.querySelectorAll('[id^="disponibles-"]').forEach((lista) => {
    new Sortable(lista, {
      group: "alergenos-edit",
      animation: 150,
    });
  });
  function actualizarInput(platoId) {
    const ul = document.getElementById(`seleccionados-${platoId}`);
    const input = document.getElementById(`inputAlergenos-${platoId}`);
    const ids = Array.from(ul.children).map((li) => li.dataset.id);
    input.value = ids.join(",");
  }

  document.querySelectorAll('[id^="edit-seleccionados-"]').forEach((lista) => {
    const platoId = lista.id.split("-")[2];
    new Sortable(lista, {
      group: "alergenos-edit",
      animation: 150,
      onAdd: () => actualizarEditInput(platoId),
      onRemove: () => actualizarEditInput(platoId),
      onSort: () => actualizarEditInput(platoId),
    });
  });
  document.querySelectorAll('[id^="edit-disponibles-"]').forEach((lista) => {
    new Sortable(lista, {
      group: "alergenos-edit",
      animation: 150,
    });
  });

  function actualizarEditInput(platoId) {
    const ul = document.getElementById(`edit-seleccionados-${platoId}`);
    const input = document.getElementById(`edit-alergenosInput-${platoId}`);
    const ids = Array.from(ul.children).map((li) => li.dataset.id);
    input.value = ids.join(",");
  }

  document
    .querySelectorAll('div[id^="modalEditarPlato-"]')
    .forEach((modalEl) => {
      const form = modalEl.querySelector("form");
      form.addEventListener("submit", async (e) => {
        e.preventDefault();
        const platoId = modalEl.id.replace("modalEditarPlato-", "");
        const fd = new FormData(form);
        const rawLimit = fd.get("limiteUnidadesDiarias")?.trim();
        const payload = {
          id: Number(platoId),
          nombre: fd.get("nombre").trim(),
          descripcion: fd.get("descripcion").trim(),
          ingredientes: fd.get("ingredientes").trim(),
          precio: parseFloat(fd.get("precio")),
          limiteUnidadesDiarias: rawLimit ? parseInt(rawLimit, 10) : null,
          alergenos: fd.get("alergenos")
            ? fd.get("alergenos").split(",").map(x => Number(x))
            : [],
        };

        try {
          const res = await fetch(`/api/plates/${platoId}`, {
            method: "PUT",
            headers: {
              "Content-Type": "application/json",
              "X-Requested-With": "XMLHttpRequest",
            },
            body: JSON.stringify(payload),
          });

          if (res.ok) {
            const updated = await res.json();
            const row = document.querySelector(`tr[data-id="${platoId}"]`);
            if (row) {
              row.querySelector('[data-field="nombre"]').textContent =
                updated.nombre;
              row.querySelector('[data-field="descripcion"]').textContent =
                updated.descripcion;
              row.querySelector('[data-field="precio"]').textContent =
                updated.precio % 1 === 0
                  ? updated.precio.toFixed(0)
                  : updated.precio.toString();
              row.querySelector('[data-field="ingredientes"]').textContent =
                updated.ingredientes;
              row.querySelector('[data-field="limiteUnidadesDiarias"]').textContent = updated.limiteUnidadesDiarias != null ? updated.limiteUnidadesDiarias : '';
              const alTd = row.querySelector('.editable-alergenos');
              alTd.innerHTML = '';

              updated.alergenos.forEach((alergeno) => {
                const { serialName } = alergeno; 
              
                const slug = serialName
                  .normalize("NFD")
                  .replace(/[\u0300-\u036f]/g, "")
                  .toLowerCase()
                  .replace(/\s+/g, "-")
                  .replace(/[^\w-]/g, "");
              
                const span = document.createElement("span");
                span.classList.add("badge", "me-1", `alergeno-${slug}`);
                span.textContent = serialName;
                alTd.appendChild(span);
              });             
            }
            
            bootstrap.Modal.getInstance(modalEl).hide();
            
            Swal.fire({
              icon: "success",
              title: "¡Plato actualizado!",
              text: "Se guardaron los cambios correctamente.",
              timer: 2000,
              showConfirmButton: false,
            });
          } else if (res.status === 400) {
            const msg = await res.text();
            Swal.fire({
              icon: "error",
              title: "Error de validación",
              text: msg,
            });
          } else {
            const msg = await res.text();
            Swal.fire({ icon: "error", title: "Error", text: msg });
          }
        } catch (err) {
          Swal.fire({
            icon: "error",
            title: "Error de red",
            text: "No se pudo conectar al servidor",
          });
          console.error(err);
        }
      });
    });

    // 1) Selecciona TODOS los formularios de borrado
    document.querySelectorAll('.form-eliminar-plato').forEach(form => {
      form.addEventListener('submit', async e => {
        e.preventDefault();
        const row = form.closest('tr');
        const url = form.action;
        // 3) Confirmación
        const { isConfirmed } = await Swal.fire({
          title: "¿Eliminar este plato?",
          icon: "warning",
          showCancelButton: true,
          confirmButtonText: "Sí, eliminar",
          cancelButtonText: "Cancelar",
        });
        if (!isConfirmed) return;

        // 4) Llamada DELETE
        try {
          const res = await fetch(url, {
            method: "DELETE",
            headers: {
              "X-Requested-With": "XMLHttpRequest"
            },
          });

          if (res.ok) {
            row.remove();
            Swal.fire({
              toast: true,
              position: "top-end",
              icon: "success",
              title: "Plato eliminado correctamente",
              showConfirmButton: false,
              timer: 3000,
            });
          } else {
            const msg = await res.text();
            Swal.fire({
              toast: true,
              position: "top-end",
              icon: "error",
              title: "Error al eliminar",
              text: msg,
              showConfirmButton: false,
              timer: 3000,
            });
          }
        } catch (err) {
          Swal.fire({
            toast: true,
            position: "top-end",
            icon: "error",
            title: "Error de red",
            text: "No se pudo conectar al servidor",
            showConfirmButton: false,
            timer: 3000,
          });
        }
      });
    });

    document.querySelectorAll('.checkbox-activo').forEach(chk => {
      chk.addEventListener('change', async () => {
        const platoId = chk.dataset.id;
        try {
          const res = await fetch(`/api/plates/${platoId}`, {
            method: 'PATCH',
            headers: {
              'Content-Type': 'application/json',
              'X-Requested-With': 'XMLHttpRequest'
            },
            body: JSON.stringify({ activo: chk.checked })
          });
          if (!res.ok) throw new Error('Error actualizando estado');
          else {
            Swal.fire({
              icon: 'success',
              toast: true,
              position: 'top-end',
              title: 'Estado del plato actualizado correctamente',
              timer: 2000,
              showConfirmButton: false
            })
            console.log("Estado del plato actualizado correctamente");
          }
        } catch (err) {
          console.error(err);
          Swal.fire({
            icon: 'error',
            title: 'No se pudo actualizar el estado',
            toast: true,
            position: 'top-end',
            timer: 2000,
            showConfirmButton: false
          });
          chk.checked = !chk.checked;
        }
      });
    });
  });