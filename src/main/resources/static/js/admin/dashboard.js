document.addEventListener('DOMContentLoaded', () => {

  /* -----------------------------------------------------------------------
   * 1. RECLUTAR EMPLEADO                                                   */
  const formRecruit = document.querySelector('#modalRecruit form');
  if (formRecruit) {
    const username = formRecruit.querySelector('#username');
    const email    = formRecruit.querySelector('#email');
    const rol      = formRecruit.querySelector('#rol');

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
      formRecruit.submit();              // ✓ OK
    });
  }

  /* Mensajes resultado reclutamiento */
  if (document.body.dataset.error) {
    new bootstrap.Modal(document.getElementById('modalRecruit')).show();
  } else if (document.body.dataset.success) {
    Swal.fire({ icon: 'success', title: '¡Correo enviado!',
                text: document.body.dataset.success, confirmButtonText: 'Aceptar' });
  }

  /* -----------------------------------------------------------------------
   * 2. CAMBIAR ESTADO DEL RESTAURANTE                                      */
  const switchRest = document.getElementById('switchRestaurante');
  if (switchRest) {
    switchRest.addEventListener('change', async () => {
      const activo = switchRest.checked;
      try {
        const res = await fetch('/api/restaurant/active', {
          method : 'PATCH',
          headers: { 'Content-Type': 'application/json',
                     'X-Requested-With': 'XMLHttpRequest' },
          body   : JSON.stringify({ activo })
        });
        if (!res.ok) throw new Error('Error actualizando estado');

        const label   = document.querySelector('label[for="switchRestaurante"]');
        const helpTxt = switchRest.closest('.card-body')
                                  .querySelector('.form-text, .text-muted.small');
        if (label)   label.textContent = activo ? 'Abierto para pedidos'
                                                : 'Cerrado (no acepta pedidos)';
        if (helpTxt) helpTxt.textContent = activo
          ? 'Puedes desactivar tu restaurante para pausar temporalmente los pedidos.'
          : 'Restaurante desactivado. Vuelve a activarlo para reanudar los pedidos.';

        Swal.fire({ icon: 'success', toast: true, position: 'top-end',
                    title: 'Estado actualizado', timer: 1500, showConfirmButton: false });
      } catch (err) {
        switchRest.checked = !activo;   // revierte
        Swal.fire({ icon: 'error', toast: true, position: 'top-end',
                    title: 'No se pudo actualizar', text: err.message,
                    timer: 2000, showConfirmButton: false });
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
      if (res.ok)  pedidosHoy.textContent = await res.text();
      else         console.error(await res.text());
    } catch (err) {
      console.error('Error al actualizar pedidos:', err);
    }
  }, 1000);

  /* -----------------------------------------------------------------------
   * 4. CATEGORÍAS DEL RESTAURANTE (drag & drop)                            */
  const catModal = document.getElementById('categoriasModal');
  if (catModal) {
    const catDisponibles   = document.getElementById('catDisponibles');
    const catSeleccionadas = document.getElementById('catSeleccionadas');

    // Sortable en ambas columnas
    [catDisponibles, catSeleccionadas].forEach(col => new Sortable(col, {
      group     : 'categorias',
      animation : 150,
      ghostClass: 'opacity-50'
    }));

    const btnGuardarCat = document.getElementById('guardarCategoriasBtn');
    const catBadges     = document.getElementById('catBadges');

    const pintaBadges = () => {
      catBadges.innerHTML = [...catSeleccionadas.querySelectorAll('li')]
        .map(li => `<span class="badge bg-secondary me-1 mb-1">${li.dataset.serial}</span>`)
        .join('');
    };

    btnGuardarCat.addEventListener('click', async () => {
      const codes = [...catSeleccionadas.querySelectorAll('li')]
                    .map(li => li.dataset.code);

      try {
        const res = await fetch('/api/restaurant/categories', {
          method : 'PATCH',
          headers: { 'Content-Type': 'application/json',
                     'X-Requested-With': 'XMLHttpRequest' },
          body   : JSON.stringify({ categorias: codes })
        });
        if (!res.ok) throw new Error(await res.text());

        pintaBadges();
        bootstrap.Modal.getInstance(catModal).hide();
        Swal.fire({ icon: 'success', toast: true, position: 'top-end',
                    title: 'Categorías actualizadas', timer: 2500, showConfirmButton: false });
      } catch (err) {
        Swal.fire({ icon: 'error', toast: true, position: 'top-end',
                    title: 'Error', text: err.message, timer: 3000, showConfirmButton: false });
        console.error(err);
      }
    });
  }

  /* -----------------------------------------------------------------------
   * 5. IMPORTE MÍNIMO DE PEDIDO                                            */
  const btnImporte = document.getElementById('btnImporte');
  if (!btnImporte) return;                 // si la card no existe, lo demás da igual

  /* Elementos & formularios ---------------------------------------------- */
  const cardText   = btnImporte.closest('.card-body').querySelector('.card-text');
  const groupBtns  = btnImporte.parentElement;
  const formFijar  = document.getElementById('formFijarImporte');
  const formEditar = document.getElementById('formEditarImporte');
  const inputFijar = document.getElementById('importeMinimo');
  const inputEditar= document.getElementById('importeMinimoEdit');

  /* Estado interno -------------------------------------------------------- */
  let currentImporte = (() => {
    const span = document.getElementById('importeActual');
    return span ? parseFloat(span.textContent.replace(',', '.')) : null;
  })();

  /* Helpers --------------------------------------------------------------- */
  const toNum  = v => parseFloat(String(v).replace(',', '.'));
  const fixed2 = n => Number(n).toFixed(2);
  const same   = v => currentImporte !== null && Math.abs(toNum(v) - currentImporte) < 0.01;

  /* Render card ----------------------------------------------------------- */
  const pintaImporte = () => {
    if (currentImporte !== null) {
      cardText.innerHTML =
        `Actualmente: <strong><span id="importeActual" class="ms-1">${fixed2(currentImporte)}</span>€</strong>`;
      btnImporte.innerHTML     = '<i class="fas fa-euro-sign me-1"></i> Editar importe';
      btnImporte.dataset.bsTarget = '#modalEditarImporte';
      toggleBtnDelete(true);
    } else {
      cardText.innerHTML     = '<em>No has fijado ningún importe mínimo.</em>';
      btnImporte.innerHTML   = '<i class="fas fa-euro-sign me-1"></i> Fijar importe';
      btnImporte.dataset.bsTarget = '#modalFijarImporte';
      toggleBtnDelete(false);
    }
  };

  /* -----------------------------------------------------------------------
   * 6. TIEMPO DE PREPARACIÓN                                               */
  const btnTiempo        = document.getElementById('btnTiempo');
  if (!btnTiempo) return;

  const tiempoCardText   = btnTiempo.closest('.card-body').querySelector('.card-text');
  const formFijarTiempo  = document.getElementById('formFijarTiempo');
  const formEditarTiempo = document.getElementById('formEditarTiempo');
  const inputFijarTiempo = document.getElementById('tiempoEstimado');
  const inputEditarTiempo= document.getElementById('tiempoEstimadoEdit');

  let currentTiempo = (() => {
    const span = document.getElementById('tiempoActual');
    return span ? parseInt(span.textContent) : null;
  })();

  const pintaTiempo = () => {
    if (currentTiempo !== null) {
      tiempoCardText.classList.remove('fst-italic');
      tiempoCardText.innerHTML =
        `Actualmente: <strong><span id="tiempoActual" class="ms-1">${currentTiempo}</span> min.</strong>`;
      if (!document.getElementById('avisoTiempoText')) {
        const p = document.createElement('p');
        p.id = 'avisoTiempoText';
        p.className = 'form-text text-muted small mb-2';
        p.textContent = 'Este tiempo se añadirá al cálculo automático de entrega que verá el cliente al hacer un pedido.';
        tiempoCardText.after(p);
      }
      btnTiempo.innerHTML = '<i class="fas fa-stopwatch me-1"></i> Editar tiempo';
      btnTiempo.dataset.bsTarget = '#modalEditarTiempo';
    } else {
      tiempoCardText.innerHTML = '<i class="bi bi-exclamation-triangle-fill fs-5 text-warning me-2"></i><em>Si no fijas un tiempo estimado de preparación, este aparecerá como “<b>No disponible</b>” para los usuarios.</em>';
      btnTiempo.innerHTML  = '<i class="fas fa-stopwatch me-1"></i> Fijar tiempo';
      btnTiempo.dataset.bsTarget = '#modalFijarTiempo';
    }
  };

  document.getElementById('modalEditarTiempo')
          ?.addEventListener('show.bs.modal', () => {
    inputEditarTiempo.value = currentTiempo ?? 10;
    inputEditarTiempo.classList.remove('is-invalid');
  });
  document.getElementById('modalFijarTiempo')
          ?.addEventListener('show.bs.modal', () => {
    inputFijarTiempo.value = '';
    inputFijarTiempo.classList.remove('is-invalid');
  });

  [formFijarTiempo, formEditarTiempo].forEach(f => f.setAttribute('novalidate', ''));

  const procesaTiempo = async input => {
    const val = parseInt(input.value);
    let msg = '';
    if (isNaN(val) || val < 10 || val > 60)       msg = 'El tiempo debe estar entre 10 y 60 minutos.';
    else if (val === currentTiempo)               msg = 'Debes introducir un tiempo distinto al actual.';

    if (msg) {
      input.nextElementSibling.textContent = msg;
      input.classList.add('is-invalid'); input.focus(); return;
    }
    input.classList.remove('is-invalid');

    try {
      const res = await fetch('/api/restaurant/preptime', {
        method : 'PATCH',
        headers: { 'Content-Type': 'application/json',
                   'X-Requested-With': 'XMLHttpRequest' },
        body   : JSON.stringify({ tiempoPreparacion: val })
      });
      if (!res.ok) throw new Error(await res.text());

      currentTiempo = val;
      pintaTiempo();
      bootstrap.Modal.getInstance(input.closest('.modal')).hide();
      Swal.fire({ icon: 'success', toast: true, position: 'top-end',
                  title: 'Tiempo guardado', timer: 3000, showConfirmButton: false });
    } catch (err) {
      Swal.fire({ icon: 'error', toast: true, position: 'top-end',
                  title: 'Error', text: err.message, timer: 3000, showConfirmButton: false });
    }
  };

  formFijarTiempo?.addEventListener('submit', e => { e.preventDefault(); procesaTiempo(inputFijarTiempo); });
  formEditarTiempo?.addEventListener('submit', e => { e.preventDefault(); procesaTiempo(inputEditarTiempo); });

  pintaTiempo();

  /* -----------------------------------------------------------------------
   * 7. BOTÓN ELIMINAR IMPORTE                                              */
  let btnDelete = document.getElementById('btnDeleteImporte');
  const toggleBtnDelete = show => {
    if (show) {
      if (!btnDelete) {
        btnDelete           = document.createElement('button');
        btnDelete.id        = 'btnDeleteImporte';
        btnDelete.type      = 'button';
        btnDelete.className = 'btn btn-outline-danger ms-2';
        btnDelete.innerHTML = '<i class="fas fa-trash-alt me-1"></i> Eliminar importe';
        groupBtns.appendChild(btnDelete);
      }
      btnDelete.classList.remove('d-none');
    } else if (btnDelete) {
      btnDelete.classList.add('d-none');
    }
  };

  groupBtns.addEventListener('click', e => {
    if (!e.target.closest('#btnDeleteImporte')) return;

    Swal.fire({
      title: 'Eliminar importe mínimo',
      text : '¿Estás seguro de que deseas eliminar el importe mínimo?',
      icon : 'warning',
      showCancelButton: true,
      confirmButtonText: 'Sí, eliminar',
      cancelButtonText : 'Cancelar'
    }).then(async res => {
      if (!res.isConfirmed) return;
      try {
        const response = await fetch('/api/restaurant/amount', {
          method : 'PATCH',
          headers: { 'Content-Type': 'application/json',
                     'X-Requested-With': 'XMLHttpRequest' },
          body   : JSON.stringify({ importeMinimo: null })
        });

        if (response.ok) {
          currentImporte = null;
          pintaImporte();
          Swal.fire({ icon: 'success', title: 'Eliminado',
                      text: 'Importe mínimo eliminado', timer: 1500,
                      showConfirmButton: false });
        } else {
          throw new Error(await response.text());
        }
      } catch (error) {
        Swal.fire({ icon: 'error', title: 'Error',
                    text: 'No se pudo eliminar el importe mínimo',
                    timer: 1500, showConfirmButton: false });
        console.error(error);
      }
    });
  });

  /* Preparar valores al abrir los modales de importe ---------------------- */
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

  [formFijar, formEditar].forEach(f => f.setAttribute('novalidate', ''));

  const procesaImporte = async input => {
    const val = toNum(input.value);
    let msg = '';
    if (isNaN(val) || val < 1)   msg = 'Debe ser ≥ 1 €.';
    else if (same(val))          msg = 'Introduce un importe distinto al actual.';

    if (msg) {
      input.nextElementSibling.textContent = msg;
      input.classList.add('is-invalid'); input.focus(); return;
    }
    input.classList.remove('is-invalid');

    try {
      const res = await fetch('/api/restaurant/amount', {
        method : 'PATCH',
        headers: { 'Content-Type': 'application/json',
                   'X-Requested-With': 'XMLHttpRequest' },
        body   : JSON.stringify({ importeMinimo: val })
      });
      if (!res.ok) throw new Error(await res.text());

      currentImporte = val;
      pintaImporte();
      bootstrap.Modal.getInstance(input.closest('.modal')).hide();
      Swal.fire({ icon: 'success', toast: true, position: 'top-end',
                  title: 'Importe guardado', timer: 3000, showConfirmButton: false });
    } catch (err) {
      Swal.fire({ icon: 'error', toast: true, position: 'top-end',
                  title: 'Error', text: err.message, timer: 3000, showConfirmButton: false });
      console.error(err);
    }
  };

  formFijar  .addEventListener('submit', e => { e.preventDefault(); procesaImporte(inputFijar);  });
  formEditar .addEventListener('submit', e => { e.preventDefault(); procesaImporte(inputEditar); });

  /* Render inicial -------------------------------------------------------- */
  pintaImporte();
});