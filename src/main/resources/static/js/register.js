document.addEventListener("DOMContentLoaded", function () {
  const apiKey = "eddca3679eed46f79e25f3aa58c75054";

  const form = document.getElementById("form-registro");

  const calleInput = document.getElementById("calle");
  const numCalleInput = document.getElementById("numCalle");
  const ciudadInput = document.getElementById("ciudad");
  const provinciaInput = document.getElementById("provincia");
  const cpInput = document.getElementById("codigoPostal");
  const dropdown = document.getElementById("sugerencias");

  const campos = {
    nombre: document.getElementById("nombre"),
    email: document.getElementById("email"),
    contrasena: document.getElementById("contrasena"),
    telefono: document.getElementById("telefono"),
    calle: calleInput,
    numCalle: numCalleInput,
    ciudad: ciudadInput,
    provincia: provinciaInput,
    codigoPostal: cpInput
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
      fetch(`https://api.geoapify.com/v1/geocode/autocomplete?text=${encodeURIComponent(query)}&lang=es&filter=countrycode:es&apiKey=${apiKey}`)
        .then(res => res.json())
        .then(data => {
          dropdown.innerHTML = '';
          if (!data.features) return;
          mostrarSugerencias(data.features);
        })
        .catch(err => console.error("Error al obtener sugerencias:", err));
    }, 300);
  });

  function mostrarSugerencias(features) {
    features.forEach(place => {
      const item = document.createElement("div");
      item.className = "autocomplete-item";
      item.textContent = place.properties.formatted;

      item.addEventListener("click", () => {
        calleInput.value = place.properties.street || place.properties.address_line1 || '';
        numCalleInput.value = place.properties.housenumber || '';
        ciudadInput.value = place.properties.city || '';
        provinciaInput.value = place.properties.state || '';
        cpInput.value = place.properties.postcode || '';
        dropdown.innerHTML = '';

        calleInput.classList.remove("is-invalid");
        numCalleInput.classList.remove("is-invalid");
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

    // Limpiar errores anteriores
    Object.entries(campos).forEach(([_, input]) => {
      input.classList.remove("is-invalid");
      const feedback = input.parentElement.querySelector(".js-feedback");
      if (feedback) {
        feedback.textContent = "";
        feedback.style.display = "none";
      }
    });

    // Validar uno por uno y mostrar solo el primer error
    for (const [key, input] of Object.entries(campos)) {
      const valor = input.value.trim();
      const feedback = input.parentElement.querySelector(".js-feedback");

      if (!valor) {
        input.classList.add("is-invalid");
        if (feedback) {
          feedback.style.display = "block";
          feedback.textContent = obtenerMensajeError(key);
        }
        valido = false;
        break;
      }

      // Validación de email
      if (key === "email" && !/^\S+@\S+\.\S+$/.test(valor)) {
        input.classList.add("is-invalid");
        if (feedback) {
          feedback.style.display = "block";
          feedback.textContent = "Introduce un correo válido";
        }
        valido = false;
        break;
      }

      // Validación de teléfono: exactamente 9 dígitos
      if (key === "telefono" && !/^\d{9}$/.test(valor)) {
        input.classList.add("is-invalid");
        if (feedback) {
          feedback.style.display = "block";
          feedback.textContent = "El teléfono debe tener 9 dígitos numéricos";
        }
        valido = false;
        break;
      }

      // Validación de contraseña
      if (key === "contrasena" && !/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[\W_]).{8,}$/.test(valor)) {
        input.classList.add("is-invalid");
        if (feedback) {
          feedback.style.display = "block";
          feedback.textContent = "La contraseña debe tener mínimo 8 caracteres, incluyendo mayúscula, minúscula, número y símbolo";
        }
        valido = false;
        break;
      }
    }

    if (!valido) e.preventDefault();
  });

  function obtenerMensajeError(key) {
    switch (key) {
      case "nombre": return "El nombre es obligatorio";
      case "email": return "El correo es obligatorio";
      case "contrasena": return "La contraseña es obligatoria";
      case "telefono": return "El teléfono es obligatorio";
      case "calle": return "La calle es obligatoria";
      case "numCalle": return "El número de la calle es obligatorio";
      case "ciudad": return "La ciudad es obligatoria";
      case "provincia": return "La provincia es obligatoria";
      case "codigoPostal": return "El código postal es obligatorio";
      default: return "Este campo es obligatorio";
    }
  }
});