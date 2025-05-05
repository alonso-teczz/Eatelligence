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

  const origen = await fetch(`/api/directions/${document.body.dataset.direccionId}`, {
    headers: { "X-Requested-With": "XMLHttpRequest" }
  }).then(res => res.json());

  document.querySelectorAll('.tiempo').forEach(async (el, i) => {
    const r = lista[i];

    console.log(r);
    

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
      el.textContent = `${totalMin == minutosEntrega ? "No disponible" : totalMin + " min." }`;
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

    const form = document.getElementById('filtrosForm');
    const data = new URLSearchParams(new FormData(form));
    const origen = await fetch(`/api/directions/${document.body.dataset.direccionId}`, {
      headers: { "X-Requested-With": "XMLHttpRequest" }
    }).then(res => res.json());
    data.set("lat", origen.lat || "");
    data.set("lon", origen.lon || "");

    const res = await fetch(`/api/restaurants?${data.toString()}`, {
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
    '<i class="bi bi-chevron-bar-left fs-5"></i>' : 
    '<i class="bi bi-chevron-bar-right fs-5"></i>';
  
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
  document.getElementById('filtrosForm')?.addEventListener('change', cargarRestaurantes);
  
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
    cardsZone.className = "d-flex flex-column justify-content-center align-items-center text-center";
    cardsZone.style.minHeight = "300px";
  
    cardsZone.innerHTML = `
      <i class="bi bi-emoji-frown fs-1 text-muted"></i>
      <p class="text-muted fs-6" style="min-width:70%">
        No se han encontrado restaurantes que cumplan con los filtros seleccionados.<br>
        Prueba a ajustar el radio de búsqueda, eliminar algún alérgeno o ampliar las categorías.
      </p>
    `;
    return;
  } else {
    cardsZone.className = "row row-cols-sm-1 row-cols-md-2 row-cols-lg-3 g-4";
    cardsZone.style.minHeight = "";
  }

  cardsZone.innerHTML = lista.map(r => `
    <div class="col-12 col-sm-12 col-md-12 col-lg-6 col-xl-6 col-xxl-4 mb-4">
    <a href="/restaurants/${r.id}" class="text-decoration-none text-reset">
        <div class="card h-100 rest" style="cursor:pointer" data-id="${r.id}">
          <div class="card-body">
            <div class="placeholder-img bg-light mb-3 rounded" style="width: 100%; aspect-ratio: 4.5 / 1.8;"></div>
            <h5 class="card-title">${r.nombreComercial}</h5>
            <p class="card-text mb-1">
              <i class="bi bi-geo-alt me-1"></i>
              ${r.ciudad}
            </p>
            <p class="card-text mb-1">
              ${
                r.importeMinimo != null
                  ? `Pedido mínimo: ${Number.isInteger(r.importeMinimo) ? r.importeMinimo : r.importeMinimo.toFixed(2)}€`
                  : 'Sin pedido mínimo'
              }
            </p>
            <p class="card-text mb-1 text-muted">
              Tiempo de entrega estimado: <span class="tiempo spinner-border spinner-border-sm text-secondary"
              data-lat="${r.latitud}" data-lon="${r.longitud}"
              role="status" aria-hidden="true"></span>
            </p>
            <p class="card-text text-muted small mb-0">
              <i class="bi bi-tags fs-6 me-1"></i>
              <span id="cat-rest-${r.id}" class="text-muted fst-italic">Cargando categorías...</span>
            </p>
          </div>
        </div>
      </a>
    </div>`).join('');

  estimarTiempos(lista);

  lista.forEach(r => {
    fetch(`/api/restaurants/${r.id}/categories`, { headers: { "X-Requested-With": "XMLHttpRequest" }})
      .then(res => res.json())
      .then(categorias => {
        const span = document.getElementById(`cat-rest-${r.id}`);
        if (span && categorias.length > 0) {
          span.textContent = categorias.map(c => c.serialName).join(" • ");
        } else if (span) {
          span.textContent = "Sin categorías";
        }
      })
      .catch(err => {
        console.error("Error al cargar categorías:", err);
      });
  });
}

// --- Main
document.addEventListener('DOMContentLoaded', () => {
  document.querySelectorAll('[data-bs-toggle="tooltip"]')
    .forEach(el => new bootstrap.Tooltip(el));
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