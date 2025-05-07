window.addEventListener("load", () => {
  /* ------------------------------------------------------------------ */
  /* ---------------------- LISTAS DRAG & DROP NUEVO ------------------ */
  /* ------------------------------------------------------------------ */
  const disponibles       = document.getElementById("alergenosDisponibles");
  const seleccionados     = document.getElementById("alergenosSeleccionados");
  const hiddenNuevo       = document.getElementById("alergenosInput");

  if (disponibles && seleccionados) {
    new Sortable(disponibles, { group: "alergenos", animation: 150 });
    new Sortable(seleccionados, {
      group: "alergenos",
      animation: 150,
      onAdd   : actualizarHiddenNuevo,
      onRemove: actualizarHiddenNuevo,
      onSort  : actualizarHiddenNuevo,
    });
  }

  function actualizarHiddenNuevo() {
    const ids = Array.from(seleccionados.children).map((li) => li.dataset.id);
    hiddenNuevo.value = ids.join(",");
  }

  /* ------------------------------------------------------------------ */
  /* ---------------------- LISTAS DRAG & DROP EDITAR ----------------- */
  /* ------------------------------------------------------------------ */

  // Por cada modal de edición pre-existente
  document.querySelectorAll('[id^="seleccionados-"]').forEach((lista) => {
    const platoId = lista.id.split("-")[1];
    new Sortable(lista, {
      group: "alergenos-edit",
      animation: 150,
      onAdd   : () => actualizarHiddenEdit(platoId),
      onRemove: () => actualizarHiddenEdit(platoId),
      onSort  : () => actualizarHiddenEdit(platoId),
    });
  });

  document.querySelectorAll('[id^="disponibles-"]').forEach((lista) => {
    new Sortable(lista, { group: "alergenos-edit", animation: 150 });
  });

  function actualizarHiddenEdit(platoId) {
    const ul     = document.getElementById(`seleccionados-${platoId}`);
    const hidden = document.getElementById(`inputAlergenos-${platoId}`);
    const ids    = Array.from(ul.children).map((li) => li.dataset.id);
    hidden.value = ids.join(",");
  }

  /* ------------------------------------------------------------------ */
  /* --- 1) INICIALIZAR TODOS LOS <input hidden> AL CARGAR LA PÁGINA -- */
  /* ------------------------------------------------------------------ */
  document.querySelectorAll('[id^="edit-alergenosInput-"]').forEach((input) => {
    const platoId = input.id.split("-")[2]; // edit-alergenosInput-<ID>
    actualizarEditInput(platoId);           // escribe los ids actuales
  });

  /* ---------- Drag & drop para los modales “inline” que genera Thymeleaf ---------- */
  document.querySelectorAll('[id^="edit-seleccionados-"]').forEach((lista) => {
    const platoId = lista.id.split("-")[2]; // edit-seleccionados-<ID>
    new Sortable(lista, {
      group: "alergenos-edit",
      animation: 150,
      onAdd   : () => actualizarEditInput(platoId),
      onRemove: () => actualizarEditInput(platoId),
      onSort  : () => actualizarEditInput(platoId),
    });
  });

  document.querySelectorAll('[id^="edit-disponibles-"]').forEach((lista) => {
    new Sortable(lista, { group: "alergenos-edit", animation: 150 });
  });

  function actualizarEditInput(platoId) {
    const ul     = document.getElementById(`edit-seleccionados-${platoId}`);
    const hidden = document.getElementById(`edit-alergenosInput-${platoId}`);
    const ids    = Array.from(ul.children).map((li) => li.dataset.id);
    hidden.value = ids.join(",");
  }

  /* ------------------------------------------------------------------ */
  /* ------------ MODALES DE EDICIÓN COMPLETA DE PLATOS ---------------- */
  /* ------------------------------------------------------------------ */
  document
    .querySelectorAll('div[id^="modalEditarPlato-"]')
    .forEach((modalEl) => {
      const form    = modalEl.querySelector("form");
      const platoId = modalEl.id.replace("modalEditarPlato-", "");

      // 2) Cada vez que se muestra el modal → refresca el hidden
      modalEl.addEventListener("shown.bs.modal", () => actualizarEditInput(platoId));

      form.addEventListener("submit", async (e) => {
        e.preventDefault();

        // 3) Garantía extra: refrescamos hidden antes de leer los datos
        actualizarEditInput(platoId);

        const fd       = new FormData(form);
        const rawLimit = fd.get("limiteUnidadesDiarias")?.trim();

        const payload = {
          id                    : Number(platoId),
          nombre                : fd.get("nombre").trim(),
          descripcion           : fd.get("descripcion").trim(),
          ingredientes          : fd.get("ingredientes").trim(),
          precio                : parseFloat(fd.get("precio")),
          limiteUnidadesDiarias : rawLimit ? parseInt(rawLimit, 10) : null,
          alergenos             : fd.get("alergenos")
                                   ? fd.get("alergenos").split(",").map(Number)
                                   : [],
        };

        /* ------------------------ llamada fetch PUT ------------------------ */
        try {
          const res = await fetch(`/api/plates/${platoId}`, {
            method : "PUT",
            headers: {
              "Content-Type"    : "application/json",
              "X-Requested-With": "XMLHttpRequest",
            },
            body: JSON.stringify(payload),
          });

          if (!res.ok) {
            const msg = await res.text();
            throw new Error(msg || "Error al actualizar");
          }

          const updated = await res.json();

          /* ------------ ACTUALIZAR LA FILA DE LA TABLA SIN RECARGAR -------- */
          const row = document.querySelector(`tr[data-id="${platoId}"]`);
          if (row) {
            row.querySelector('[data-field="nombre"]').textContent                = updated.nombre;
            row.querySelector('[data-field="descripcion"]').textContent           = updated.descripcion;
            row.querySelector('[data-field="precio"]').textContent               =
              updated.precio % 1 === 0 ? updated.precio.toFixed(0) : updated.precio;
            row.querySelector('[data-field="ingredientes"]').textContent          = updated.ingredientes;
            row.querySelector('[data-field="limiteUnidadesDiarias"]').textContent =
              updated.limiteUnidadesDiarias ?? "";

            const alTd = row.querySelector(".editable-alergenos");
            alTd.innerHTML = "";
            updated.alergenos.forEach(({ serialName }) => {
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
            toast: true,
            position: "top-end",
            title: "Plato actualizado",
            timer: 2000,
            showConfirmButton: false,
          });
        } catch (err) {
          Swal.fire({
            icon : "error",
            title: "Error",
            text : err.message || "No se pudo actualizar",
          });
          console.error(err);
        }
      });
    });

    function getCsrf() {
      const token  = document.querySelector('#csrfToken').getAttribute('content');
      const header = document.querySelector('#csrfHeader').getAttribute('content');
      return { header, token };
    }

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
        const { header, token } = getCsrf();
        try {
          const res = await fetch(url, {
            method: "DELETE",
            credentials: 'same-origin',
            headers: {
              "X-Requested-With": "XMLHttpRequest",
              [header]: token
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
        const { header, token } = getCsrf();
        try {
          const res = await fetch(`/api/plates/${platoId}`, {
            method: 'PATCH',
            credentials: 'same-origin',
            headers: {
              'Content-Type': 'application/json',
              "X-Requested-With": "XMLHttpRequest",
              [header]: token
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