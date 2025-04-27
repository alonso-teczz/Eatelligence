document.addEventListener('DOMContentLoaded', function() {
    if (document.body.getAttribute('data-error')) {
      // Abre el modal
      const modalEl = document.getElementById('modalRecruit');
      const modal = new bootstrap.Modal(modalEl);
      modal.show();
    } else if (document.body.getAttribute('data-success')) {
      Swal.fire({
        icon: 'success',
        title: '¡Correo enviado!',
        text: document.body.getAttribute('data-success'),
        confirmButtonText: 'Aceptar'
      });
    }
});  