document.addEventListener("DOMContentLoaded", function () {
  const apiKey = "eddca3679eed46f79e25f3aa58c75054";

  const formUsuario = document.getElementById("form-usuario");
  const formRestaurante = document.getElementById("form-restaurante");
  const containerUsuario = document.querySelector(".container-usuario");
  const containerRestaurante = document.querySelector(".container-restaurante");
  const checkbox = document.getElementById("activarRestaurante");
  const btnRegistro = document.getElementById("btn-registro");

  const togglePassword = document.getElementById("togglePassword");
  const iconoPassword = document.getElementById("iconoPassword");

  const calleInput = document.getElementById("calle");
  const numCalleInput = document.getElementById("numCalle");
  const ciudadInput = document.getElementById("ciudad");
  const provinciaInput = document.getElementById("provincia");
  const cpInput = document.getElementById("codigoPostal");
  const dropdown = document.getElementById("sugerencias");

  let timeout;

  const datosComunes = {};

  function guardarDatosFormulario(form) {
    const inputs = form.querySelectorAll("input, textarea");
    inputs.forEach(input => {
      if (input.id) datosComunes[input.id] = input.value;
    });
  }

  function restaurarDatosFormulario(form) {
    const inputs = form.querySelectorAll("input, textarea");
    inputs.forEach(input => {
      if (input.id && datosComunes[input.id]) {
        input.value = datosComunes[input.id];
      }
    });
  }

  function toggleFormularios(esRestaurante) {
    guardarDatosFormulario(esRestaurante ? formUsuario : formRestaurante);

    formUsuario.classList.remove("mostrar");
    formRestaurante.classList.remove("mostrar");
    containerUsuario.classList.remove("mostrar");
    containerRestaurante.classList.remove("mostrar");

    if (esRestaurante) {
      formRestaurante.classList.add("mostrar");
      containerRestaurante.classList.add("mostrar");
      checkbox.checked = true;
      restaurarDatosFormulario(formRestaurante);
    } else {
      formUsuario.classList.add("mostrar");
      containerUsuario.classList.add("mostrar");
      checkbox.checked = false;
      restaurarDatosFormulario(formUsuario);
    }

    mostrarAcordeon();
  }

  function mostrarAcordeon() {
    const contenido = document.getElementById("contenido-acordeon-restaurante");
    if (contenido && !contenido.classList.contains("show")) {
      new bootstrap.Collapse(contenido, { toggle: false }).show();
    }
  }

  toggleFormularios(checkbox.checked);

  checkbox.addEventListener("change", () => {
    toggleFormularios(checkbox.checked);
  });

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

  togglePassword?.addEventListener("click", () => {
    const formActivo = document.querySelector(".form-registro.mostrar");
    const contrasena = formActivo?.querySelector("#contrasena");
    if (!contrasena) return;

    const isPassword = contrasena.type === "password";
    contrasena.type = isPassword ? "text" : "password";
    iconoPassword.classList.toggle("bi-eye");
    iconoPassword.classList.toggle("bi-eye-slash");
  });

  btnRegistro?.addEventListener("click", () => {
    const formActivo = document.querySelector(".form-registro.mostrar");
    if (!formActivo) return;

    let valido = true;

    const allInputs = formActivo.querySelectorAll("input, textarea");
    allInputs.forEach(input => input.classList.remove("is-invalid"));

    const campos = {
      nombre: formActivo.querySelector("#nombre"),
      email: formActivo.querySelector("#email"),
      contrasena: formActivo.querySelector("#contrasena"),
      telefono: formActivo.querySelector("#telefono"),
      calle: formActivo.querySelector("#calle"),
      numCalle: formActivo.querySelector("#numCalle"),
      ciudad: formActivo.querySelector("#ciudad"),
      provincia: formActivo.querySelector("#provincia"),
      codigoPostal: formActivo.querySelector("#codigoPostal")
    };

    for (const [key, input] of Object.entries(campos)) {
      const valor = input?.value.trim();
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

    const inputContrasena = formActivo?.querySelector("#contrasena");
    const inputRepetir = formActivo?.querySelector("#repetirContrasena");
    
    if (valido && inputRepetir) {
      const repetirValor = inputRepetir.value.trim();
    
      // Elimina mensajes anteriores
      inputRepetir.classList.remove("is-invalid");
    
      // Obtenemos el div de feedback
      const feedbackDiv = inputRepetir.nextElementSibling;
      
      if (!repetirValor) {
        inputRepetir.classList.add("is-invalid");
        if (feedbackDiv) feedbackDiv.textContent = "Repite la contraseña";
        inputRepetir.focus();
        valido = false;
      } else if (inputContrasena && inputContrasena.value !== repetirValor) {
        inputRepetir.classList.add("is-invalid");
        if (feedbackDiv) feedbackDiv.textContent = "Las contraseñas no coinciden";
        inputRepetir.focus();
        valido = false;
      }
    }

    if (valido && checkbox.checked) {
      const nombreCom = formActivo.querySelector("#nombreComercial");
      const desc = formActivo.querySelector("#descripcion");

      if (!nombreCom || nombreCom.value.trim().length < 6) {
        nombreCom.classList.add("is-invalid");
        nombreCom.focus();
        valido = false;
      } else if (!desc || desc.value.trim().length < 6) {
        desc.classList.add("is-invalid");
        desc.focus();
        valido = false;
      }
    }

    if (valido) {
      if (inputRepetir) inputRepetir.disabled = true;
      formActivo.requestSubmit();
    }
  });
});
