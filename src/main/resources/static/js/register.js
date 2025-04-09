document.addEventListener("DOMContentLoaded", function () {
  const apiKey = "eddca3679eed46f79e25f3aa58c75054";

  const form = document.getElementById("form-registro");

  const calleInput = document.getElementById("calle");
  const numCalleInput = document.getElementById("numCalle");
  const ciudadInput = document.getElementById("ciudad");
  const provinciaInput = document.getElementById("provincia");
  const cpInput = document.getElementById("codigoPostal");
  const dropdown = document.getElementById("sugerencias");

  const contrasena = document.getElementById("contrasena");
  const repetirContrasena = document.getElementById("repetirContrasena");
  const togglePassword = document.getElementById("togglePassword");
  const iconoPassword = document.getElementById("iconoPassword");

  const checkboxRestaurante = document.getElementById("activarRestaurante");
  const acordeonRestaurante = document.getElementById("form-restaurante");
  const nombreComercial = document.getElementById("nombreComercial");
  const descripcion = document.getElementById("descripcion");

  const campos = {
    nombre: document.getElementById("nombre"),
    email: document.getElementById("email"),
    contrasena: contrasena,
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
        provinciaInput.value = place.properties.county || '';
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

  togglePassword.addEventListener("click", () => {
    const isPassword = contrasena.type === "password";
    contrasena.type = isPassword ? "text" : "password";
    iconoPassword.classList.toggle("bi-eye");
    iconoPassword.classList.toggle("bi-eye-slash");
  });

  checkboxRestaurante?.addEventListener("change", () => {
    if (checkboxRestaurante.checked) {
      new bootstrap.Collapse(acordeonRestaurante, { show: true });
      nombreComercial.setAttribute("required", "required");
      descripcion.setAttribute("required", "required");
    } else {
      new bootstrap.Collapse(acordeonRestaurante, { toggle: false }).hide();
      nombreComercial.removeAttribute("required");
      descripcion.removeAttribute("required");
      nombreComercial.value = "";
      descripcion.value = "";
      nombreComercial.classList.remove("is-invalid");
      descripcion.classList.remove("is-invalid");
    }
  });

  form.addEventListener("submit", function (e) {
    let valido = true;

    Object.entries(campos).forEach(([_, input]) => {
      input.classList.remove("is-invalid");
    });
    contrasena.classList.remove("is-invalid");
    repetirContrasena.classList.remove("is-invalid");
    nombreComercial?.classList.remove("is-invalid");
    descripcion?.classList.remove("is-invalid");

    for (const [key, input] of Object.entries(campos)) {
      const valor = input.value.trim();

      if (!valor) {
        input.classList.add("is-invalid");
        input.focus();
        valido = false;
        break;
      }

      if (key === "email" && !/^\S+@\S+\.\S+$/.test(valor)) {
        input.classList.add("is-invalid");
        input.focus();
        valido = false;
        break;
      }

      if (key === "telefono" && !/^\d{9}$/.test(valor)) {
        input.classList.add("is-invalid");
        input.focus();
        valido = false;
        break;
      }

      if (key === "contrasena" && !/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[\W_]).{8,}$/.test(valor)) {
        input.classList.add("is-invalid");
        input.focus();
        valido = false;
        break;
      }
    }

    // Validar coincidencia de contraseñas
    if (valido && contrasena.value.trim() !== repetirContrasena.value.trim()) {
      repetirContrasena.classList.add("is-invalid");
      repetirContrasena.focus();
      valido = false;
    }

    // Validación extra para restaurante si está activo
    if (valido && checkboxRestaurante?.checked) {
      if (nombreComercial.value.trim().length < 6) {
        nombreComercial.classList.add("is-invalid");
        nombreComercial.focus();
        valido = false;
      } else if (descripcion.value.trim().length < 6) {
        descripcion.classList.add("is-invalid");
        descripcion.focus();
        valido = false;
      }
    }

    if (checkboxRestaurante?.checked) {
      form.setAttribute("action", "/validRestReg");
    } else {
      form.setAttribute("action", "/validUserReg");
    }

    if (!valido) {
      e.preventDefault();
    } else {
      repetirContrasena.disabled = true;
    }
  });
});
