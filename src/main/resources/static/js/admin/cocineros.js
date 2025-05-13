document.querySelectorAll('.form-eliminar-cocinero').forEach(form => {
    form.addEventListener('submit', async e => {
      e.preventDefault();
      const row = form.closest('tr');
      const url = form.action;
      // 3) Confirmación
      const { isConfirmed } = await Swal.fire({
        title: "¿Eliminar este empleado?",
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
          // no necesitas Content-Type para DELETE sin body
        });

        if (res.ok) {
          // 5) Eliminamos la fila del DOM
          row.remove();
          Swal.fire({
            toast: true,
            position: "top-end",
            icon: "success",
            title: "Empleado eliminado correctamente",
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