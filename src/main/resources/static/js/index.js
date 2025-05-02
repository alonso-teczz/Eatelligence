// --- Actualiza el label del range
function updateRadioLabel(e) {
  document.getElementById('radioLabel').textContent = e.target.value;
}

// --- Llama a Geoapify para calcular el tiempo estimado
async function estimarTiempos(lista) {
  const apiKey = await fetch('/api/apikeys/geoapify', {
    headers: { "X-Requested-With": "XMLHttpRequest" }
  }).then(res => res.json())
    .then(data => data.apiKey);

  const origen = await fetch(`/api/direction/${document.body.dataset.direccionId}`, {
    headers: { "X-Requested-With": "XMLHttpRequest" }
  }).then(res => res.json());

  document.querySelectorAll('.tiempo').forEach(async (el, i) => {
    const r = lista[i];

    try {
      const url = `https://api.geoapify.com/v1/routing?waypoints=${origen.lat},${origen.lon}|${r.latitud},${r.longitud}&mode=drive&apiKey=${apiKey}`;
      const res = await fetch(url);
      const data = await res.json();

      const segundos = data.features?.[0]?.properties?.time || 0;
      const minutosEntrega = Math.ceil(segundos / 60);
      const totalMin = minutosEntrega + (r.tiempoPreparacion ?? 0);

      el.classList.remove("spinner-border", "spinner-border-sm", "text-secondary");
      el.removeAttribute("role");
      el.removeAttribute("aria-hidden");
      el.textContent = `${totalMin} min.`;
    } catch (err) {
      el.classList.remove("spinner-border", "spinner-border-sm", "text-secondary");
      el.textContent = '—';
    }
  });
}

// --- Lógica para cargar restaurantes con filtros
async function cargarRestaurantes() {
  const spinner = document.getElementById('spinnerCargando');
  const cardsZone = document.getElementById("restaurant-cards");
  
  try {
    spinner?.classList.remove("d-none");
    cardsZone.querySelectorAll(".card").forEach(card => card.remove());

    const form = document.getElementById('filtroForm');
    const data = new URLSearchParams(new FormData(form));
    const origen = await fetch(`/api/direction/${document.body.dataset.direccionId}`, {
      headers: { "X-Requested-With": "XMLHttpRequest" }
    }).then(res => res.json());
    data.set("lat", origen.lat || "");
    data.set("lon", origen.lon || "");

    const res = await fetch(`/api/restaurant?${data.toString()}`, {
      headers: { "X-Requested-With": "XMLHttpRequest" }
    });

    if (!res.ok) throw new Error('Error al cargar restaurantes');
    const page = await res.json();
    renderCards(page.content);
  } catch (err) {
    console.error(err);
  } finally {
    spinner?.classList.add("d-none");
  }
}

// Corrección para la función toggleFiltrosPanel
function toggleFiltrosPanel() {
  // Corrección: usamos el ID 'filtros' que aparece en la imagen en lugar de 'filtrosPanel'
  const filtrosPanel = document.getElementById('filtros');
  const restaurantCards = document.getElementById('restaurant-cards');
  const toggleBtn = document.getElementById('toggleFiltros');
  
  // Alternar la visibilidad del panel
  filtrosPanel.classList.toggle('d-none');
  
  // Cambiar el texto del botón según la visibilidad del panel
  const isPanelVisible = !filtrosPanel.classList.contains('d-none');
  toggleBtn.innerHTML = isPanelVisible ? 
    '<i class="bi bi-x-lg me-1"></i> Ocultar filtros' : 
    '<i class="bi bi-funnel me-1"></i> Mostrar filtros';
  
  // Ajustar la grilla a 3 columnas con panel visible o 4 columnas con panel oculto
  if (isPanelVisible) {
    restaurantCards.classList.remove('row-cols-1', 'row-cols-md-4', 'row-cols-lg-4');
    restaurantCards.classList.add('row-cols-1', 'row-cols-md-3', 'row-cols-lg-3');
  } else {
    restaurantCards.classList.remove('row-cols-1', 'row-cols-md-3', 'row-cols-lg-3');
    restaurantCards.classList.add('row-cols-1', 'row-cols-md-4', 'row-cols-lg-4');
  }
}

// Corrección para la función setupFilters
function setupFilters() {
  document.getElementById('radioRange')?.addEventListener('input', updateRadioLabel);
  document.getElementById('filtroForm')?.addEventListener('change', cargarRestaurantes);
  
  // Setup para el botón de toggle
  const toggleBtn = document.getElementById('toggleFiltros');
  if (toggleBtn) {
    toggleBtn.addEventListener('click', toggleFiltrosPanel);
  }
  
  // Inicializar la grilla en función de si el panel está visible inicialmente
  // Corrección: usamos el ID 'filtros' que aparece en la imagen
  const filtrosPanel = document.getElementById('filtros');
  const restaurantCards = document.getElementById('restaurant-cards');
  
  if (filtrosPanel && restaurantCards) {
    const isPanelVisible = !filtrosPanel.classList.contains('d-none');
    if (isPanelVisible) {
      restaurantCards.classList.add('row-cols-1', 'row-cols-md-3', 'row-cols-lg-3');
    } else {
      restaurantCards.classList.add('row-cols-1', 'row-cols-md-4', 'row-cols-lg-4');
    }
  }
}

// Corrección para la función renderCards para mejorar la visualización de las cards
function renderCards(lista) {
  const cardsZone = document.getElementById("restaurant-cards");
  if (!lista.length) {
    cardsZone.innerHTML = '<p class="text-muted">No hay restaurantes cerca.</p>';
    return;
  }

  cardsZone.innerHTML = lista.map(r => `
  <div class="col mb-4">
    <div class="card h-100 rest" style="cursor:pointer" data-id="${r.id}">
      <div class="card-body">
        <h5 class="card-title">${r.nombreComercial}</h5>
        <p class="card-text mb-1">
          <i class="bi bi-geo-alt"></i>
          ${r.ciudad}
        </p>
        <p class="card-text mb-1">
          ${
            r.importeMinimo != null
              ? `Pedido mínimo: €${Number.isInteger(r.importeMinimo) ? r.importeMinimo : r.importeMinimo.toFixed(2)}`
              : 'Sin pedido mínimo'
          }
        </p>
        <p class="card-text mb-1 text-muted">
          Tiempo de entrega estimado: <span class="tiempo spinner-border spinner-border-sm text-secondary"
          data-lat="${r.latitud}" data-lon="${r.longitud}"
          role="status" aria-hidden="true"></span>
        </p>
      </div>
    </div>
  </div>`).join('');

  estimarTiempos(lista);
}

// --- Main
document.addEventListener('DOMContentLoaded', () => {
  setupFilters();

  const modalEl = document.getElementById('direccionModal');
  if (modalEl) {
    const dirModal = new bootstrap.Modal(modalEl, {
      backdrop: 'static',
      keyboard: false
    });
    dirModal.show();

    modalEl.querySelector('form')?.addEventListener('submit', (e) => {
      const seleccionado = modalEl.querySelector('input[name="direccionId"]:checked');
      if (!seleccionado) {
        e.preventDefault();
        const msg = document.getElementById('modalError');
        msg.textContent = 'Selecciona una dirección antes de continuar.';
        msg.classList.remove('d-none');
      }
    });
  } else if (document.getElementById("restaurant-cards")) {
    cargarRestaurantes();
  } 
});