// --- Actualiza la etiqueta del range del radio ---
function updateRadioLabel(e) {
  document.getElementById('radioLabel').textContent = e.target.value;
}

// --- Renderiza las cards en el contenedor #cards ---
function renderCards(restaurantes) {
  const container = document.getElementById('restaurant-cards');
  container.innerHTML = '';

  if (!restaurantes.length) {
    container.innerHTML = `<p class="text-muted">No hay restaurantes cerca.</p>`;
    return;
  }

  restaurantes.forEach(r => {
    const card = document.createElement('div');
    card.className = 'card';
    card.style.width = '18rem';
    card.innerHTML = `
      <div class="card-body">
        <h5 class="card-title">${r.nombreComercial}</h5>
        <p class="card-text">
          Precio medio: €${r.precioMedio.toFixed(2)}<br>
          Distancia: ${r.distancia.toFixed(2)} km
        </p>
        <a href="/restaurante/${r.id}" class="btn btn-primary">Ver más</a>
      </div>
    `;
    container.appendChild(card);
  });
}

// --- Lee los filtros y carga los restaurantes ---
async function cargarRestaurantes() {
  try {
    const form = document.getElementById('filtroForm');
    const data = new URLSearchParams(new FormData(form));
    data.append("lat", document.body.dataset.latEnvio);
    data.append("lon", document.body.dataset.lonEnvio);
    const res = await fetch(`/api/restaurant?${data.toString()}`, { headers: { "X-Requested-With": "XMLHttpRequest" }});
    if (!res.ok) throw new Error('Error al cargar restaurantes');
    const page = await res.json();
    renderCards(page.content);
  } catch (err) {
    console.error(err);
    alert(err.message);
  }
}

// --- Inicializa listeners de filtro ---
function setupFilters() {
  document.getElementById('radioRange')
  ?.addEventListener('input', updateRadioLabel);
  document.getElementById('filtroForm')
  ?.addEventListener('change', cargarRestaurantes);
}

// --- Main ---
document.addEventListener('DOMContentLoaded', () => {

  setupFilters();
  
  const modalEl = document.getElementById('direccionModal');
  if (modalEl) {
    const dirModal = new bootstrap.Modal(modalEl, {
      backdrop: 'static',
      keyboard: false
    });
    dirModal.show();

    // Validación de selección al enviar el formulario
    const form = modalEl.querySelector('form');
    form.addEventListener('submit', (e) => {
      const seleccionado = form.querySelector('input[name="direccionId"]:checked');
      if (!seleccionado) {
        e.preventDefault();
        const msg = document.getElementById('modalError');
        if (msg) {
          msg.textContent = 'Selecciona una dirección antes de continuar.';
          msg.classList.remove('d-none');
        }
      }
    });
  } else if (document.getElementById("restaurant-cards")) {
    cargarRestaurantes();
  }
});