document.addEventListener('DOMContentLoaded', () => {

  /* -----------------------------------------------------------------------
   * 1. RECLUTAR EMPLEADO                                                   */
  const formRecruit = document.querySelector('#modalRecruit form');
  if (formRecruit) {
    const username = formRecruit.querySelector('#username');
    const email = formRecruit.querySelector('#email');
    const rol = formRecruit.querySelector('#rol');

    formRecruit.addEventListener('submit', e => {
      e.preventDefault();
      formRecruit.querySelectorAll('.is-invalid').forEach(el => el.classList.remove('is-invalid'));

      if (username.value.trim().length < 6) {
        username.classList.add('is-invalid'); username.focus(); return;
      }
      if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email.value.trim())) {
        email.classList.add('is-invalid'); email.focus(); return;
      }
      if (!rol.value) {
        rol.classList.add('is-invalid'); rol.focus(); return;
      }
      formRecruit.submit();   // validación OK -> envía
    });
  }

  /* Mensajes resultado reclutamiento */
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

  /* -----------------------------------------------------------------------
   * 2. CAMBIAR ESTADO DEL RESTAURANTE (switch)                             */
  const switchRest = document.getElementById('switchRestaurante');
  if (switchRest) {
    switchRest.addEventListener('change', async () => {
      const activo = switchRest.checked;
      try {
        const res = await fetch('/api/restaurant/active', {
          method: 'PATCH',
          headers: { 'Content-Type': 'application/json', 'X-Requested-With': 'XMLHttpRequest' },
          body: JSON.stringify({ activo })
        });
        if (!res.ok) throw new Error('Error actualizando estado');

        /* Actualiza textos de la card */
        const label = document.querySelector('label[for="switchRestaurante"]');
        const helpTxt = switchRest.closest('.card-body').querySelector('.form-text, .text-muted.small');
        if (label) label.textContent = activo ? 'Abierto para pedidos' : 'Cerrado (no acepta pedidos)';
        if (helpTxt) helpTxt.textContent = activo
          ? 'Puedes desactivar tu restaurante para pausar temporalmente los pedidos.'
          : 'Restaurante desactivado. Vuelve a activarlo para reanudar los pedidos.';

        Swal.fire({
          icon: 'success', toast: true, position: 'top-end',
          title: 'Estado actualizado', timer: 1500, showConfirmButton: false
        });
      } catch (err) {
        switchRest.checked = !activo;   // revierte
        Swal.fire({
          icon: 'error', title: 'No se pudo actualizar', text: err.message,
          toast: true, position: 'top-end', timer: 2000, showConfirmButton: false
        });
      }
    });
  }

  /* -----------------------------------------------------------------------
   * 3. PEDIDOS DE HOY – actualización cada segundo                         */
  setInterval(async () => {
    const pedidosHoy = document.getElementById('pedidosHoy');
    if (!pedidosHoy) return;

    try {
      const res = await fetch('/api/orders/today',
        { headers: { 'X-Requested-With': 'XMLHttpRequest' } });
      if (res.ok) pedidosHoy.textContent = await res.text();
      else console.error(await res.text());
    } catch (err) {
      console.error('Error al actualizar pedidos:', err);
    }
  }, 1000);

  /* -----------------------------------------------------------------------
   * 4. IMPORTE MÍNIMO DE PEDIDO                                            */
  const btnImporte = document.getElementById('btnImporte');
  if (!btnImporte) return;                     // la card no está en esta vista

  /* --- Elementos & formularios --- */
  const cardText = btnImporte.closest('.card-body').querySelector('.card-text');
  const groupBtns = btnImporte.parentElement;
  const formFijar = document.getElementById('formFijarImporte');
  const formEditar = document.getElementById('formEditarImporte');
  const inputFijar = document.getElementById('importeMinimo');
  const inputEditar = document.getElementById('importeMinimoEdit');

  /* --- Estado interno --- */
  let currentImporte = (() => {
    const span = document.getElementById('importeActual');
    return span ? parseFloat(span.textContent.replace(',', '.')) : null;
  })();

  /* Helpers */
  const toNum = v => parseFloat(String(v).replace(',', '.'));
  const fixed2 = n => Number(n).toFixed(2);
  const same = v => currentImporte !== null && Math.abs(toNum(v) - currentImporte) < 0.01;

  /* Render card */
  const pintaCard = () => {
    if (currentImporte !== null) {
      cardText.innerHTML =
        `Actualmente: <strong><span id="importeActual" class="ms-1">${fixed2(currentImporte)}</span>€</strong>`;
      btnImporte.innerHTML = '<i class="fas fa-euro-sign me-1"></i> Editar importe';
      btnImporte.dataset.bsTarget = '#modalEditarImporte';
      toggleBtnDelete(true);
    } else {
      cardText.innerHTML = '<em>No has fijado ningún importe mínimo.</em>';
      btnImporte.innerHTML = '<i class="fas fa-euro-sign me-1"></i> Fijar importe';
      btnImporte.dataset.bsTarget = '#modalFijarImporte';
      toggleBtnDelete(false);
    }
  };

  // 5. TIEMPO DE PREPARACIÓN
  const btnTiempo = document.getElementById('btnTiempo');
  if (!btnTiempo) return;

  const tiempoCardText = btnTiempo.closest('.card-body').querySelector('.card-text');
  const formFijarTiempo = document.getElementById('formFijarTiempo');
  const formEditarTiempo = document.getElementById('formEditarTiempo');
  const inputFijarTiempo = document.getElementById('tiempoEstimado');
  const inputEditarTiempo = document.getElementById('tiempoEstimadoEdit');  

  let currentTiempo = (() => {
    const span = document.getElementById('tiempoActual');
    return span ? parseInt(span.textContent) : null;
  })();

  const pintaTiempo = () => {
    if (currentTiempo !== null) {
      tiempoCardText.classList.remove("fst-italic");
      tiempoCardText.innerHTML =
        `Actualmente: <strong><span id="tiempoActual" class="ms-1">${currentTiempo}</span> min.</strong>`;
      if (!document.getElementById("avisoTiempoText")) {
        const avisoCalculoText = document.createElement("p");
        avisoCalculoText.innerHTML = `<p class="form-text text-muted small mb-2">
        Este tiempo se añadirá al cálculo automático de entrega que verá el cliente al hacer un pedido.
        </p>`;
        tiempoCardText.after(avisoCalculoText);
      }
      btnTiempo.innerHTML = '<i class="fas fa-stopwatch me-1"></i> Editar tiempo';
      btnTiempo.dataset.bsTarget = '#modalEditarTiempo';
    } else {
      tiempoCardText.innerHTML = '<em>No has fijado ningún tiempo estimado.</em>';
      btnTiempo.innerHTML = '<i class="fas fa-stopwatch me-1"></i> Fijar tiempo';
      btnTiempo.dataset.bsTarget = '#modalFijarTiempo';
    }
  };

  document.getElementById('modalEditarTiempo')?.addEventListener('show.bs.modal', () => {
    inputEditarTiempo.value = currentTiempo ?? 10;
    inputEditarTiempo.classList.remove('is-invalid');
  });
  document.getElementById('modalFijarTiempo')?.addEventListener('show.bs.modal', () => {
    inputFijarTiempo.value = '';
    inputFijarTiempo.classList.remove('is-invalid');
  });

  [formFijarTiempo, formEditarTiempo].forEach(f => f.setAttribute('novalidate', ''));

  const procesaTiempo = async input => {
    const val = parseInt(input.value);
    let msg = '';
    if (isNaN(val) || val < 10 || val > 60) {
      msg = 'El tiempo de preparación debe estar comprendido entre 10 y 60 minutos.';
    } else if (val === currentTiempo) {
      msg = 'Debes introducir un tiempo distinto al actual.';
    }    

    if (msg) {
      input.nextElementSibling.textContent = msg;
      input.classList.add('is-invalid');
      input.focus();
      return;
    }

    input.classList.remove('is-invalid');

    try {
      const res = await fetch('/api/restaurant/preptime', {
        method: 'PATCH',
        headers: { 'Content-Type': 'application/json', 'X-Requested-With': 'XMLHttpRequest' },
        body: JSON.stringify({ tiempoPreparacion: val })
      });
      if (!res.ok) throw new Error(await res.text());

      currentTiempo = val;
      pintaTiempo();
      bootstrap.Modal.getInstance(input.closest('.modal')).hide();
      Swal.fire({ icon: 'success', title: 'Tiempo guardado', toast: true, position: 'top-end', timer: 3000, showConfirmButton: false });
    } catch (err) {
      Swal.fire({ icon: 'error', title: 'Error', text: err.message, toast: true, position: 'top-end', timer: 3000, showConfirmButton: false });
    }
  };

  formFijarTiempo?.addEventListener('submit', e => { e.preventDefault(); procesaTiempo(inputFijarTiempo); });
  formEditarTiempo?.addEventListener('submit', e => { e.preventDefault(); procesaTiempo(inputEditarTiempo); });

  pintaTiempo();

  /* Botón Eliminar */
  let btnDelete = document.getElementById('btnDeleteImporte');
  const toggleBtnDelete = show => {
    if (show) {
      if (!btnDelete) {
        btnDelete = document.createElement('button');
        btnDelete.id = 'btnDeleteImporte';
        btnDelete.type = 'button';
        btnDelete.className = 'btn btn-outline-danger ms-2';
        btnDelete.innerHTML = '<i class="fas fa-trash-alt me-1"></i> Eliminar importe';
        groupBtns.appendChild(btnDelete);
      }
      btnDelete.classList.remove('d-none');
    } else if (btnDelete) {
      btnDelete.classList.add('d-none');
    }
  };

  /* Confirmación SweetAlert2 */
  groupBtns.addEventListener('click', e => {
    if (!e.target.closest('#btnDeleteImporte')) return;

    Swal.fire({
      title: 'Eliminar importe mínimo',
      text: '¿Estás seguro de que deseas eliminar el importe mínimo?',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Sí, eliminar',
      cancelButtonText: 'Cancelar'
    }).then(async res => {
      if (!res.isConfirmed) return;
      const response = await fetch('/api/restaurant/amount', {
        method: 'PATCH',
        headers: {
          'Content-Type': 'application/json',
          'X-Requested-With': 'XMLHttpRequest'
        },
        body: JSON.stringify({ importeMinimo: null })
      });

      try {
        if (response.ok) {
          currentImporte = null;
          pintaCard();
          Swal.fire({
            icon: 'success', title: 'Eliminado', text: 'Importe mínimo eliminado',
            timer: 1500, showConfirmButton: false
          });
        } else {
          Swal.fire({
            icon: 'error', title: 'Error', text: 'No se pudo eliminar el importe mínimo',
            timer: 1500, showConfirmButton: false
          });
          console.error(await response.text());
        }
      } catch (error) {
        Swal.fire({
          toast: true,
          position: "top-end",
          icon: "error",
          title: "Error de red",
          text: "No se pudo conectar al servidor",
          showConfirmButton: false,
          timer: 3000,
        })
        console.error(error);
      }
    });
  });

  /* Preparar valores al abrir los modales */
  document.getElementById('modalEditarImporte')
    .addEventListener('show.bs.modal', () => {
      inputEditar.value = fixed2(currentImporte ?? 1);
      inputEditar.classList.remove('is-invalid');
    });
  document.getElementById('modalFijarImporte')
    .addEventListener('show.bs.modal', () => {
      inputFijar.value = '';
      inputFijar.classList.remove('is-invalid');
    });

  /* Validación personalizada (sin tooltips nativos) */
  [formFijar, formEditar].forEach(f => f.setAttribute('novalidate', ''));

  /* --- Validación y envío ------------------------------------------------- */
  const procesa = async (input) => {
    const val = toNum(input.value);
    let msg = '';

    if (isNaN(val) || val < 1) msg = 'Debes introducir un importe mayor o igual a 1 €.';
    else if (same(val)) msg = 'Debes introducir un importe distinto al actual.';

    if (msg) {
      input.nextElementSibling.textContent = msg;
      input.classList.add('is-invalid');
      input.focus();
      return;
    }
    input.classList.remove('is-invalid');

    /* --- Llamada al backend ------------------------------------------------ */
    try {
      const res = await fetch('/api/restaurant/amount', {
        method: 'PATCH',
        headers: { 'Content-Type': 'application/json', 'X-Requested-With': 'XMLHttpRequest' },
        body: JSON.stringify({ importeMinimo: val })
      });
      if (!res.ok) throw new Error(await res.text());

      currentImporte = val;                          // actualizamos estado local
      pintaCard();
      bootstrap.Modal.getInstance(input.closest('.modal')).hide();
      Swal.fire({
        icon: 'success', toast: true, position: 'top-end',
        title: 'Importe guardado', timer: 3000, showConfirmButton: false
      });
    } catch (err) {
      Swal.fire({
        icon: 'error', toast: true, position: 'top-end',
        title: 'Error', text: err.message, timer: 3000, showConfirmButton: false
      });
      console.error(err);
    }
  };

  /* --- Listeners de los formularios --------------------------------------- */
  formFijar.addEventListener('submit', async e => { e.preventDefault(); await procesa(inputFijar); });
  formEditar.addEventListener('submit', async e => { e.preventDefault(); await procesa(inputEditar); });

  /* Render inicial */
  pintaCard();
});
