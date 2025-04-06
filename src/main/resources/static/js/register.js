document.addEventListener("DOMContentLoaded", function () {
  const apiKey = document.body.dataset.geoapifyKey;

  const form = document.getElementById("form-registro");
  const calleInput = document.getElementById("calle");
  const dropdown = document.getElementById("sugerencias");
  const ciudadInput = document.getElementById("ciudad");
  const provinciaInput = document.getElementById("provincia");
  const cpInput = document.getElementById("codigoPostal");

  const campos = {
    nombre: document.getElementById("nombre"),
    email: document.getElementById("email"),
    contrasena: document.getElementById("contrasena"),
    telefono: document.getElementById("telefono"),
    calle: calleInput
  };

  let timeout;

  calleInput.addEventListener("input", () => {
    const query = calleInput.value.trim();

    clearTimeout(timeout);
    if (query.length < 3) {
      dropdown.innerHTML = '';
      return;
    }

    timeout = setTimeout(() => {
      fetch(`https://api.geoapify.com/v1/geocode/autocomplete?text=${encodeURIComponent(query)}&lang=es&limit=5&filter=countrycode:es&apiKey=${apiKey}`)
        .then(res => res.json())
        .then(data => {
          dropdown.innerHTML = '';
          if (!data.features) return;
          mostrarSugerencias(data.features);
        })
        .catch(err => {
          console.error("Error al obtener sugerencias:", err);
        });
    }, 300);
  });

  function mostrarSugerencias(features) {
    features.forEach(place => {
      const item = document.createElement("div");
      item.className = "autocomplete-item";
      item.textContent = place.properties.formatted;
      item.addEventListener("click", () => {
        calleInput.value = place.properties.formatted;
        ciudadInput.value = place.properties.city || '';
        provinciaInput.value = place.properties.state || '';
        cpInput.value = place.properties.postcode || '';
        dropdown.innerHTML = '';
        calleInput.classList.remove("is-invalid");
      });
      dropdown.appendChild(item);
    });
  }

  document.addEventListener("click", function (e) {
    if (!dropdown.contains(e.target) && e.target !== calleInput) {
      dropdown.innerHTML = '';
    }
  });

  form.addEventListener("submit", function (e) {
    let valido = true;

    // Limpiar clases antes de validar
    Object.values(campos).forEach(input => input.classList.remove("is-invalid"));

    // Validar uno por uno y detener en el primer error
    for (const [key, input] of Object.entries(campos)) {
      const valor = input.value.trim();

      if (!valor) {
        input.classList.add("is-invalid");
        valido = false;
        break;
      }

      if (key === "email" && !/^\S+@\S+\.\S+$/.test(valor)) {
        input.classList.add("is-invalid");
        valido = false;
        break;
      }
    }

    // Validar que la dirección tenga ciudad, provincia y CP
    if (valido && (!ciudadInput.value || !provinciaInput.value || !cpInput.value)) {
      calleInput.classList.add("is-invalid");
      valido = false;
    }

    if (!valido) {
      e.preventDefault();
    }
  });
});