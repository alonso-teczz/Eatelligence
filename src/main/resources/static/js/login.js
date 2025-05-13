document.addEventListener('DOMContentLoaded', () => {
    const form = document.querySelector('form');
    const usernameInput = form.querySelector('[name="username"]');
    const passwordInput = form.querySelector('[name="password"]');
  
    const globalError = document.querySelector('#global-error') || null;
  
    form.addEventListener('submit', (e) => {
      e.preventDefault();
      // Ocultar errores del frontend previos
      if (globalError) {
        globalError.style.display = 'none';
      }

      usernameInput.classList.remove('is-invalid');
      passwordInput.classList.remove('is-invalid');
  
      // Validación
      if (!usernameInput.value.trim()) {
        usernameInput.classList.add('is-invalid');
        usernameInput.focus();
        return;
      }
  
      if (!passwordInput.value.trim()) {
        passwordInput.classList.add('is-invalid');
        passwordInput.focus();
        return;
      }

      form.submit();
    });
  
    // Mostrar modal de logout
    if (document.body.dataset.logoutSuccess === 'true') {
      Swal.fire({
        toast: true,
        position: 'top-end',
        icon: 'success',
        title: 'Sesión cerrada correctamente',
        showConfirmButton: false,
        timer: 3000,
        timerProgressBar: true,
        background: '#e6ffed',
        color: '#198754',
        customClass: {
          popup: 'border-0 shadow-sm rounded-3',
          title: 'fw-semibold',
          icon: 'text-success'
        },
        didOpen: (toast) => {
          toast.addEventListener('mouseenter', Swal.stopTimer);
          toast.addEventListener('mouseleave', Swal.resumeTimer);
        }
      });
    }
});  