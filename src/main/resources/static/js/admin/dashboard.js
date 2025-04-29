document.addEventListener('DOMContentLoaded', function() {
  const formRecruit = document.querySelector('#modalRecruit form');
  if (formRecruit) {
    const username = formRecruit.querySelector('#username');
    const email = formRecruit.querySelector('#email');
    const rol = formRecruit.querySelector('#rol');
    formRecruit.addEventListener('submit', e => {
      e.preventDefault();
      formRecruit.querySelectorAll('.is-invalid').forEach(el => el.classList.remove('is-invalid'));
      if (username.value.trim().length < 6) {
        username.classList.add('is-invalid');
        username.focus();
        return;
      }
      if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email.value.trim())) {
        email.classList.add('is-invalid');
        email.focus();
        return;
      }
      if (!rol.value) {
        rol.classList.add('is-invalid');
        rol.focus();
        return;
      }
      formRecruit.submit();
    });
  }
  
  if (document.body.getAttribute('data-error')) {
    new bootstrap.Modal(document.getElementById('modalRecruit')).show();
  } else if (document.body.getAttribute('data-success')) {
    Swal.fire({
      icon: 'success',
      title: '¡Correo enviado!',
      text: document.body.getAttribute('data-success'),
      confirmButtonText: 'Aceptar'
    });
  }


  const switchRest = document.getElementById('switchRestaurante');
  if (switchRest) {
    switchRest.addEventListener('change', async () => {
      const activo = switchRest.checked;
      try {
        const res = await fetch('/api/restaurant/active', {
          method: 'PATCH',
          headers: {
            'Content-Type': 'application/json',
            'X-Requested-With': 'XMLHttpRequest'
          },
          body: JSON.stringify({ activo })
        });
        if (!res.ok) throw new Error('Error actualizando estado');
        const label = document.querySelector('label[for="switchRestaurante"]');
        if (label) {
          label.textContent = activo
            ? 'Abierto para pedidos'
            : 'Cerrado (no acepta pedidos)';
        }
        const helpText = switchRest
          .closest('.card-body')
          .querySelector('.form-text, .text-muted.small');
        if (helpText) {
          helpText.textContent = activo
            ? 'Puedes desactivar tu restaurante para pausar temporalmente los pedidos.'
            : 'Restaurante desactivado. Vuelve a activarlo para reanudar los pedidos.';
        }
        Swal.fire({
          icon: 'success',
          toast: true,
          position: 'top-end',
          title: 'Estado del restaurante actualizado correctamente',
          timer: 1500,
          showConfirmButton: false
        });
      } catch (err) {
        switchRest.checked = !activo;
        Swal.fire({
          icon: 'error',
          title: 'No se pudo actualizar el estado',
          text: err.message,
          toast: true,
          position: 'top-end',
          timer: 2000,
          showConfirmButton: false
        });
      }
    });
  }

  setInterval(async () => {
    const pedidosHoy = document.getElementById('pedidosHoy');
    if (!pedidosHoy) return;
    
    try {
      const res = await fetch('/api/orders/today', {
        method: 'GET',
        headers: { 'X-Requested-With': 'XMLHttpRequest' }
      });
      if (res.ok) {
        pedidosHoy.textContent = await res.text();
      } else {
        console.error(await res.text());
      }
    } catch (error) {
      console.error('Error al actualizar pedidos:', error);
    }
  }, 500);
});