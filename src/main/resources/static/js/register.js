document.addEventListener("DOMContentLoaded", function () {
  const apiKey = "eddca3679eed46f79e25f3aa58c75054";

  const formUsuario = document.getElementById("form-usuario");
  const formRestaurante = document.getElementById("form-restaurante");
  const containerUsuario = document.querySelector(".container-usuario");
  const containerRestaurante = document.querySelector(".container-restaurante");
  const checkbox = document.getElementById("activarRestaurante");
  const acordeon = document.getElementById("acordeon-restaurante");
  const btnSubmitUsuario = containerUsuario.querySelector("button[type='button']");

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

  // Autocompletado de dirección
  calleInput?.addEventListener("input", () => {
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

  // Mostrar/ocultar contraseña
  togglePassword?.addEventListener("click", () => {
    const isPassword = contrasena.type === "password";
    contrasena.type = isPassword ? "text" : "password";
    iconoPassword.classList.toggle("bi-eye");
    iconoPassword.classList.toggle("bi-eye-slash");
  });

  // Forzar mostrar el acordeón (si existe)
  function mostrarAcordeon() {
    const contenido = document.getElementById("contenido-acordeon-restaurante");
    if (contenido && !contenido.classList.contains("show")) {
      new bootstrap.Collapse(contenido, { toggle: false }).show();
    }
  }  

  // Alternar visibilidad entre formularios
  function toggleFormularios(esRestaurante) {
    if (esRestaurante) {
      containerUsuario.classList.remove("mostrar");
      containerRestaurante.classList.add("mostrar");
      checkbox.checked = true;
    } else {
      containerRestaurante.classList.remove("mostrar");
      containerUsuario.classList.add("mostrar");
      checkbox.checked = false;
  
      // Limpiar formulario restaurante
      formRestaurante.querySelectorAll("input, textarea").forEach(el => {
        el.value = "";
        el.classList.remove("is-invalid");
      });
  
      formRestaurante.querySelectorAll("[disabled]").forEach(el => el.removeAttribute("disabled"));
    }
  
    mostrarAcordeon();
  }  

  // Estado inicial
  toggleFormularios(checkbox.checked);

  // Evento cambio de checkbox
  checkbox.addEventListener("change", () => {
    if (checkbox.checked) {
      containerUsuario.classList.add("d-none");
      containerUsuario.style.display = "none";
  
      containerRestaurante.classList.remove("d-none");
      containerRestaurante.style.display = "block";
  
      mostrarAcordeon(); // 👈 Esto activa el acordeón
    } else {
      containerRestaurante.classList.add("d-none");
      containerRestaurante.style.display = "none";
  
      containerUsuario.classList.remove("d-none");
      containerUsuario.style.display = "block";
    }
  });  

  // Disparar el submit del formulario usuario desde su botón
  btnSubmitUsuario?.addEventListener("click", () => {
    formUsuario?.requestSubmit();
  });

  // Validación al enviar cualquiera de los formularios (form-restaurante tiene botón propio tipo submit)
  const form = document.querySelector(".form-registro");
  form?.addEventListener("submit", function (e) {
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

      if (key === "contrasena" && !/^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[\\W_]).{8,}$/.test(valor)) {
        input.classList.add("is-invalid");
        input.focus();
        valido = false;
        break;
      }
    }

    if (valido && contrasena.value.trim() !== repetirContrasena.value.trim()) {
      repetirContrasena.classList.add("is-invalid");
      repetirContrasena.focus();
      valido = false;
    }

    if (valido && checkbox.checked) {
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

    if (checkbox.checked) {
      form.setAttribute("action", "/validRestaurantReg");
    } else {
      form.setAttribute("action", "/validClientReg");
    }

    if (!valido) {
      e.preventDefault();
    } else {
      repetirContrasena.disabled = true;
    }
  });

  const btnRegistro = document.getElementById("btn-registro");

  btnRegistro?.addEventListener("click", () => {
    if (checkbox.checked) {
      formRestaurante?.requestSubmit();
    } else {
      formUsuario?.requestSubmit();
    }
  });
});
