let pageLinks = {};

/**
 * Actualiza el texto del <span> que muestra el valor del
 * deslizador de radio de búsqueda en la página principal.
 * @param {Event} e - Evento que contiene el valor actual del
 *                    deslizador.
 */
function updateRadioLabel(e) {
  document.getElementById("radioLabel").textContent = e.target.value;
}

/**
 * Estima el tiempo de entrega a cada uno de los restaurantes de la
 * lista dada y muestra el resultado en la interfaz de usuario.
 * @param {Array} lista - Arreglo de restaurantes con sus
 *                        propiedades latitud y longitud.
 * @return {Promise} Promesa que se resuelve cuando se han
 *                   estimado los tiempos de entrega.
 */
async function estimarTiempos(lista) {
  const apiKey = await fetch("/api/apikeys/geoapify", {
    headers: { "X-Requested-With": "XMLHttpRequest" },
  })
    .then((res) => res.json())
    .then((data) => data.apiKey);

  const origen = await fetch(
    `/api/directions/${document.body.dataset.direccionId}`,
    { headers: { "X-Requested-With": "XMLHttpRequest" } }
  ).then((res) => res.json());

  document.querySelectorAll(".tiempo").forEach(async (el, i) => {
    const r = lista[i];
    try {
      const url = `https://api.geoapify.com/v1/routing?waypoints=${origen.lat},${origen.lon}|${r.latitud},${r.longitud}&mode=scooter&apiKey=${apiKey}`;
      const res = await fetch(url);
      const data = await res.json();

      const segundos = data.features?.[0]?.properties?.time || 0;
      const minutosEntrega = Math.ceil(segundos / 60);
      const totalMin = minutosEntrega + (r.tiempoPreparacion ?? 0);

      el.classList.remove(
        "spinner-border",
        "spinner-border-sm",
        "text-secondary"
      );
      el.removeAttribute("role");
      el.removeAttribute("aria-hidden");
      el.textContent =
        totalMin == minutosEntrega ? "No disponible" : totalMin + " min.";
    } catch {
      el.classList.remove(
        "spinner-border",
        "spinner-border-sm",
        "text-secondary"
      );
      el.textContent = "—";
    }
  });
}

/* ——————————————————————————————————————————————————————— */
/* -------------  CARGA RESTAURANTES + FILTROS ------------- */
/* ——————————————————————————————————————————————————————— */
// variables globales de paginación
let currentPage = 0;
let totalPages  = 1;

/**
 * Carga la lista de restaurantes visible en la interfaz, teniendo en cuenta
 * los filtros de categorías, alérgenos, radio, importe y horario. La
 * función primero obtiene la latitud y longitud de la dirección actual
 * y luego construye la petición GET para obtener la lista de restaurantes
 * que cumplen los filtros.
 *
 * @async
 * @returns {Promise<void>}
 */
async function cargarRestaurantes() {
  const spinner   = document.getElementById("spinnerCargando");
  const cardsZone = document.getElementById("restaurant-cards");

  try {
    spinner.classList.remove("d-none");
    cardsZone.querySelectorAll(".card").forEach(c => c.remove());

    // 1) filtros
    const form = document.getElementById("filtrosForm");
    const data = new URLSearchParams(new FormData(form));

    // 2) page y size
    data.set("page", currentPage);
    const size = document.getElementById("pageSize").value || "9";
    data.set("size", size);

    // 3) lat/lon, día y hora (igual que antes)…
    const origen = await fetch(`/api/directions/${document.body.dataset.direccionId}`, { headers:{"X-Requested-With":"XMLHttpRequest"} }).then(r=>r.json());
    data.set("lat", origen.lat||""); data.set("lon",origen.lon||"");
    const now = new Date();
    data.set("dia", now.toLocaleDateString("en-GB",{weekday:"long",timeZone:"Europe/Madrid"}).toUpperCase());
    data.set("hora", now.toLocaleTimeString("it-IT",{hour12:false,timeZone:"Europe/Madrid"}));

    // 4) petición
    const url = "/api/restaurants?"+data.toString();
    console.log("📡 Fetch",url);
    const res = await fetch(url, { headers:{"X-Requested-With":"XMLHttpRequest"} });
    if(!res.ok) throw new Error("Error al cargar restaurantes");
    const page = await res.json();

    // 5) actualizar totalPages y deshabilitar botones
    totalPages = page.totalPages;
    document.getElementById("prevPage").classList.toggle("disabled", currentPage <= 0);
    document.getElementById("nextPage").classList.toggle("disabled", currentPage >= totalPages-1);

    // (opcional) indicador
    const ind = document.getElementById("pageIndicator");
    if(ind) ind.textContent = `${currentPage+1} / ${totalPages}`;

    // 6) pintar cards
    renderCards(page.content);

  } catch (e) {
    console.error(e);
    cardsZone.className =
      "d-flex flex-column justify-content-center align-items-center text-center";
    cardsZone.style.minHeight = "300px";
    cardsZone.innerHTML = `
      <i class="bi bi-emoji-frown fs-1 text-muted"></i>
      <p class="text-muted fs-6" style="min-width:70%">
        No se han encontrado restaurantes que cumplan con los filtros seleccionados.<br>
        Prueba a ajustar el radio de búsqueda, eliminar algún alérgeno o ampliar las categorías.
      </p>`;
  } finally {
    spinner.classList.add("d-none");
  }
}

document.getElementById("prevPage").addEventListener("click", e => {
  e.preventDefault();
  if (currentPage > 0) {
    currentPage--;
    cargarRestaurantes().then(() => {
      window.scrollTo({ top: 0, behavior: 'smooth' });
    });
  }
});
document.getElementById("nextPage").addEventListener("click", e => {
  e.preventDefault();
  if (currentPage < totalPages - 1) {
    currentPage++;
    cargarRestaurantes().then(() => {
      window.scrollTo({ top: 0, behavior: 'smooth' });
    });
  }
});


/**
 * Alterna la visibilidad del panel de filtros en la interfaz de usuario.
 * Actualiza el botón de alternancia para reflejar el estado actual del panel
 * de filtros y ajusta el diseño de las tarjetas de restaurantes según la
 * visibilidad del panel.
 */
function toggleFiltrosPanel() {
  const filtrosPanel = document.getElementById("filtros");
  const restaurantCards = document.getElementById("restaurant-cards");
  const toggleBtn = document.getElementById("toggleFiltros");

  filtrosPanel.classList.toggle("d-none");
  const isPanelVisible = !filtrosPanel.classList.contains("d-none");
  toggleBtn.innerHTML = isPanelVisible
    ? '<i class="bi bi-chevron-bar-left fs-5"></i>'
    : '<i class="bi bi-chevron-bar-right fs-5"></i>';

  if (isPanelVisible) {
    restaurantCards.classList.add("row-cols-md-2");
    restaurantCards.classList.remove("row-cols-md-3");
  } else {
    restaurantCards.classList.add("row-cols-md-3");
    restaurantCards.classList.remove("row-cols-md-2");
  }
}


/**
 * Configura los eventos de los controles de filtrado en la interfaz de usuario
 * para recargar la lista de restaurantes según sea necesario.
 * Algunos controles se manejan de forma diferente según su tipo:
 * - El slider de radio de búsqueda se actualiza en vivo según el usuario
 *   cambia el valor, y se recarga la lista al soltar.
 * - Los cambios en checkboxes o numéricos generales se recargan inmediatamente.
 * - El input de texto de búsqueda por nombre se recarga solo cuando se
 *   pierde el foco y el valor ha cambiado.
 * - El botón para mostrar/ocultar el panel de filtros ajusta el diseño de
 *   las tarjetas según sea necesario.
 * @returns {void}
 */
function setupFilters() {
  const form            = document.getElementById('filtrosForm');
  const filtrosPanel    = document.getElementById('filtros');
  const restaurantCards = document.getElementById('restaurant-cards');

  if (!form) return;            // por seguridad

  /* ------------ 1) SLIDER “radioRange” ---------------- */
  const radio = form.querySelector('#radioRange');
  if (radio) {
    // mientras se mueve → solo actualiza el label
    radio.addEventListener('input', updateRadioLabel);
    // al soltar (change) → recarga resultados
    radio.addEventListener('change', () => cargarRestaurantes());
  }

  /* ------------ 2) CAMBIOS GENERALES EN EL FORM ------------- */
  // Cualquier cambio en checkboxes o numéricos provoca recarga inmediata
  form.addEventListener('change', e => {
    if (e.target === radio) return;       // el slider ya se gestiona arriba
    if (e.target.name === 'nombre') return; // el nombre lo manejamos aparte
    cargarRestaurantes();
  });

  /* ------------ 3) INPUT “Buscar por nombre” ---------------- */
  const nombreInput = form.querySelector('input[name="nombre"]');
  if (nombreInput) {
    let ultimoValor = nombreInput.value.trim();   // evita peticiones duplicadas

    // perder el foco → si cambió el texto, recarga
    nombreInput.addEventListener('blur', () => {
      const actual = nombreInput.value.trim();
      if (actual !== ultimoValor) {
        ultimoValor = actual;
        cargarRestaurantes();
      }
    });

    // pulsar Enter dentro del campo → fuerza blur y evita submit
    nombreInput.addEventListener('keydown', e => {
      if (e.key === 'Enter') {
        e.preventDefault();
        nombreInput.blur();                // dispara el blur anterior
      }
    });
  }

  /* ------------ 4) BOTÓN MOSTRAR/OCULTAR FILTROS ---------- */
  document.getElementById('toggleFiltros')
          ?.addEventListener('click', toggleFiltrosPanel);

  /* ------------ 5) AJUSTE INICIAL DE LA GRILLA ------------- */
  if (filtrosPanel && restaurantCards) {
    const visible = !filtrosPanel.classList.contains('d-none');
    restaurantCards.classList.add(visible ? 'row-cols-md-2' : 'row-cols-md-3');
  }
}

/**
 * Renderiza una lista de restaurantes en el contenedor especificado.
 * Si no se proporciona el contenedor, se utiliza el elemento con id
 * "restaurant-cards".
 *
 * @param {Array<Restaurant>} lista - Arreglo de objetos Restaurant
 * @param {HTMLElement} [container=document.getElementById("restaurant-cards")]
 * @returns {void}
 */
function renderCards(lista, container = null) {
  const cardsZone = container ?? document.getElementById("restaurant-cards");

  /* mensaje cuando no hay resultados */
  if (!lista.length) {
    cardsZone.className =
      "d-flex flex-column justify-content-center align-items-center text-center";
    cardsZone.style.minHeight = "300px";
    cardsZone.innerHTML = `
      <i class="bi bi-emoji-frown fs-1 text-muted"></i>
      <p class="text-muted fs-6" style="min-width:70%">
        No se han encontrado restaurantes que cumplan con los filtros seleccionados.<br>
        Prueba a ajustar el radio de búsqueda, eliminar algún alérgeno o ampliar las categorías.
      </p>`;
    return;
  } else {
    cardsZone.className =
      "row row-cols-sm-1 row-cols-md-2 row-cols-lg-3 g-4";
    cardsZone.style.minHeight = "";
  }

  cardsZone.innerHTML = lista
    .map(
      (r) => `
    <div class="col-12 col-sm-12 col-md-12 col-lg-6 col-xl-6 col-xxl-4 mb-4">
      <a href="/restaurants/${r.id}" class="text-decoration-none text-reset">
        <div class="card h-100 rest" style="cursor:pointer" data-id="${r.id}">
          <div class="card-body">
            <div class="placeholder-img bg-light mb-3 rounded" style="width:100%;aspect-ratio:4.5/1.8;"></div>
            <h5 class="card-title">${r.nombreComercial}</h5>

            <p class="text-muted mb-1">
              <i class="bi bi-geo-alt-fill me-1"></i>${r.ciudad}
            </p>

            <p class="card-text mb-1 text-muted">
              Tiempo de entrega estimado: <span
                class="tiempo spinner-border spinner-border-sm text-secondary"
                data-lat="${r.latitud}" data-lon="${r.longitud}"
                role="status" aria-hidden="true"></span>
            </p>

            <p class="text-muted mb-1">
              ${
                r.importeMinimo != null
                  ? `Pedido mínimo: ${
                      Number.isInteger(r.importeMinimo)
                        ? r.importeMinimo
                        : r.importeMinimo.toFixed(2)
                    }€`
                  : "Sin pedido mínimo"
              }
            </p>

            <p class="text-muted mb-1">
              <i class="bi bi-clock me-1"></i>${r.horarioDia ?? "—"}
            </p>

            <p class="mb-1">
              <i class="bi ${
                r.abiertoAhora ? "bi-door-open" : "bi-door-closed"
              } me-1"></i>
              <span class="${r.abiertoAhora ? "text-muted" : "text-danger"}">
                ${
                  r.horarioDia == null
                    ? "—"
                    : r.abiertoAhora
                    ? "Abierto"
                    : "Cerrado"
                }
              </span>
            </p>

            <p id="cat-rest-${r.id}" class="text-muted small mb-0">
              <i class="bi bi-tags fs-6 me-1"></i>
              ${
                r.categorias?.length
                  ? r.categorias.join(" • ")
                  : "Sin categorías"
              }
            </p>
          </div>
        </div>
      </a>
    </div>`
    )
    .join("");

  estimarTiempos(lista);

  /* vuelve a traer categorías para actualizar el <p id="cat-rest-"> */
  lista.forEach((r) => {
    fetch(`/api/restaurants/${r.id}/categories`, {
      headers: { "X-Requested-With": "XMLHttpRequest" },
    })
      .then((res) => res.json())
      .then((categorias) => {
        const span = document.getElementById(`cat-rest-${r.id}`);
        if (!span) return;
        span.textContent = categorias.length
          ? categorias.map((c) => c.serialName).join(" • ")
          : "Sin categorías";
      })
      .catch((err) => console.error("Error al cargar categorías:", err));
  });
}

/**
 * Actualiza los enlaces de paginación prev/next y sus estilos.
 * @param {object} links - Contiene las propiedades `prev` y `next` que
 *   indican si hay una página previa o siguiente, respectivamente.
 *   Dichas propiedades se usan para habilitar/deshabilitar los botones
 *   de la paginación.
 */
function updatePageLinks(links) {
  pageLinks = links;
  document
    .getElementById("prevPage")
    .classList.toggle("disabled", !links.prev);
  document
    .getElementById("nextPage")
    .classList.toggle("disabled", !links.next);
}

/* ——————————————————————————————————————————————————————— */
/* -------------------------- MAIN ------------------------- */
/* ——————————————————————————————————————————————————————— */
document.addEventListener("DOMContentLoaded", () => {
  const verifyBtn = document.getElementById('checkout-verify-btn');
  if (verifyBtn) {
    verifyBtn.addEventListener('click', function(){
      Swal.fire({
        title: 'Cuenta no verificada',
        text: 'Para finalizar tu compra debes verificar tu cuenta.',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonText: 'Verificar cuenta',
        cancelButtonText: 'Cancelar',
        confirmButtonColor: '#ffc107',
        cancelButtonColor: '#6c757d'
      }).then(result => {
        if (result.isConfirmed) {
          window.location.href = '/pending-verification';
        }
      });
    });
  }
  
  setupFilters();

  // botones prev / next
  document.getElementById("prevPage").addEventListener("click", (e) => {
    e.preventDefault();
    if (pageLinks.prev) cargarRestaurantes(pageLinks.prev);
  });
  document.getElementById("nextPage").addEventListener("click", (e) => {
    e.preventDefault();
    if (pageLinks.next) cargarRestaurantes(pageLinks.next);
  });

  // modal dirección (si existe) o primera carga
  const modalEl = document.getElementById("direccionModal");
  if (modalEl) {
    const dirModal = new bootstrap.Modal(modalEl, {
      backdrop: "static",
      keyboard: false,
    });
    dirModal.show();
    modalEl
      .querySelector("form")
      ?.addEventListener("submit", (e) => {
        const seleccionado = modalEl.querySelector(
          'input[name="direccionId"]:checked'
        );
        if (!seleccionado) {
          e.preventDefault();
          const msg = document.getElementById("modalError");
          msg.textContent = "Selecciona una dirección antes de continuar.";
          msg.classList.remove("d-none");
        }
      });
  } else {
    cargarRestaurantes();
  }
});
