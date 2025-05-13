document.addEventListener('DOMContentLoaded', () => {
  const disp  = document.getElementById('catDisponibles');
  const sel   = document.getElementById('catSeleccionadas');
  const input = document.getElementById('categoriasInput');

  /* ---------- NUEVO: función slugify común a HTML y badges ---------- */
  const slugify = (txt) =>
    txt.normalize('NFD')                 // 1. descompone tíldes
       .replace(/[\u0300-\u036f]/g, '')  // 2. elimina marcas diacríticas
       .toLowerCase()
       .trim()
       .replace(/\s+/g, '-')             // 3. espacios → guiones
       .replace(/[^\w-]/g, '');          // 4. resta caracteres “raros”

  /* ---------------------------- Sortable ---------------------------- */
  if (disp && sel) {
    new Sortable(disp, { group: 'categorias', animation: 150 });
    new Sortable(sel,  {
      group: 'categorias',
      animation: 150,
      onAdd   : actualizarHidden,
      onRemove: actualizarHidden,
      onSort  : actualizarHidden
    });
  }

  /**
   * Actualiza el valor del input hidden con los ids de las categorías
   * seleccionadas en el sortable. Se llama en cada evento de
   * reordenamiento o cambio de selección.
   */
  function actualizarHidden() {
    const ids = Array.from(sel.children).map(li => li.dataset.id);
    input.value = ids.join(',');
  }

  /* --------------------------- Guardar ----------------------------- */
  document.getElementById('btnGuardarCategorias')
          ?.addEventListener('click', async () => {

    actualizarHidden();                     // asegura el valor del input
    const ids = input.value
      ? input.value.split(',').map(Number)
      : [];

    try {
      const res = await fetch('/api/restaurants/categories', {
        method : 'PATCH',
        headers: {
          'Content-Type'    : 'application/json',
          'X-Requested-With': 'XMLHttpRequest'
        },
        body   : JSON.stringify(ids)
      });

      if (!res.ok) throw new Error(await res.text());

      /* --- Re-pinta los badges en la card --- */
      const lista = document.getElementById('listaCategorias');
      lista.innerHTML = '';

      sel.querySelectorAll('li').forEach(li => {
        const badge      = document.createElement('span');
        const slug       = slugify(li.textContent);
        badge.className  = `badge categoria-${slug} me-1 mb-1`;
        badge.dataset.id = li.dataset.id;
        badge.textContent= li.textContent;
        lista.appendChild(badge);
      });

      bootstrap.Modal.getInstance(
        document.getElementById('modalCategorias')
      ).hide();

      Swal.fire({
        icon            : 'success',
        toast           : true,
        position        : 'top-end',
        title           : 'Categorías actualizadas',
        timer           : 2000,
        showConfirmButton: false
      });

    } catch (err) {
      console.error(err);
      Swal.fire({
        icon : 'error',
        title: 'Error',
        text : err.message || 'No se pudo guardar',
      });
    }
  });

  /* ----- mantiene el hidden sincronizado al cerrar el modal -------- */
  document.getElementById('modalCategorias')
          ?.addEventListener('hidden.bs.modal', actualizarHidden);
});
