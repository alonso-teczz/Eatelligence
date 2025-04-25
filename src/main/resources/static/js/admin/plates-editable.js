document.addEventListener('DOMContentLoaded', function () {
  try {
      const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
      tooltipTriggerList.forEach(function (el) {
        new bootstrap.Tooltip(el, { container: 'body' });
      });
  } catch (e) {
      console.error('Error inicializando tooltips:', e);
  }

  const tablaPlatos = document.getElementById('tablaPlatos');
  tablaPlatos.addEventListener('dblclick', function (e) {
      const celda = e.target.closest('.editable-alergenos');
      if (!celda) return;

      const platoId = celda.getAttribute('data-id');
      if (!platoId) return;

      const modal = new bootstrap.Modal(document.getElementById('modalEditarAlergenos-' + platoId));
      modal.show();
  });
});