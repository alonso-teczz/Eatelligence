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

/**
 * Guarda los valores de los campos de entrada dentro del formulario dado.
 * 
 * Esta función itera sobre cada elemento de entrada y textarea dentro del formulario proporcionado.
 * Si un elemento tiene un ID, el valor del campo se guarda en el objeto `datosComunes` con el ID 
 * como clave.
 * 
 * @param {HTMLFormElement} form - El elemento formulario que contiene las entradas a guardar.
 */

  function guardarDatosFormulario(form) {
    const inputs = form.querySelectorAll("input, textarea");
    inputs.forEach(input => {
      if (input.id) datosComunes[input.id] = input.value;
    });
  }

/**
 * Restaura los valores de los campos de entrada dentro del formulario dado utilizando datos guardados.
 * 
 * Esta función itera sobre cada elemento de entrada y textarea dentro del formulario proporcionado.
 * Si un elemento tiene un ID y un valor correspondiente ha sido previamente guardado en el 
 * objeto `datosComunes`, la función restaura el valor del campo al valor guardado.
 * 
 * @param {HTMLFormElement} form - El elemento formulario que contiene las entradas a restaurar.
 */

  function restaurarDatosFormulario(form) {
    const inputs = form.querySelectorAll("input, textarea");
    inputs.forEach(input => {
      if (input.id && datosComunes[input.id]) {
        input.value = datosComunes[input.id];
      }
    });
  }


/**
 * Muestra u oculta los formularios de usuario y restaurante.
 *
 * El parámetro esRestaurante indica si se debe mostrar el formulario del restaurante o del usuario.
 * La función guarda los valores de los campos de entrada del formulario que se va a ocultar,
 * y los restaura en el formulario que se va a mostrar.
 *
 * @param {boolean} esRestaurante - true si se debe mostrar el formulario del restaurante, false en caso contrario.
 */

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
      configurarTogglePassword(formRestaurante);
    } else {
      formUsuario.classList.add("mostrar");
      containerUsuario.classList.add("mostrar");
      checkbox.checked = false;
      restaurarDatosFormulario(formUsuario);
      configurarTogglePassword(formUsuario);
    }    

    showAccordion();
  }

/**
 * Asegura que la sección del acordeón para los detalles del restaurante esté visible.
 * Si el contenido del acordeón no está ya visible, utiliza el componente Collapse
 * de Bootstrap para mostrar el contenido sin cambiar su estado actual.
 */
  function showAccordion() {
    const accordionContent = document.getElementById("contenido-acordeon-restaurante");
    if (accordionContent && !accordionContent.classList.contains("show")) {
      new bootstrap.Collapse(accordionContent, { toggle: false }).show();
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

  /**
   * Muestra las sugerencias de direcciones en un dropdown
   * @param {object[]} features - Array de objetos con propiedades para cada lugar
   * @prop {string} properties.formatted - Dirección formatada
   * @prop {string} [properties.street] - Calle
   * @prop {string} [properties.address_line1] - Línea de dirección
   * @prop {string} [properties.housenumber] - Número de la calle
   * @prop {string} [properties.city] - Ciudad
   * @prop {string} [properties.county] - Provincia o ciudad
   * @prop {string} [properties.postcode] - Código postal
   */
  function mostrarSugerencias(features) {
    features.forEach(place => {
      const item = document.createElement("div");
      item.className = "autocomplete-item";
      item.textContent = place.properties.formatted;

      item.addEventListener("click", () => {
        calleInput.value = place.properties.street || place.properties.address_line1 || '';
        numCalleInput.value = place.properties.housenumber || '';
        ciudadInput.value = place.properties.city || '';
        provinciaInput.value = place.properties.county || place.properties.city;
        cpInput.value = place.properties.postcode || '';
        dropdown.innerHTML = '';
        document.getElementById("latitud").value = place.properties.lat;
        document.getElementById("longitud").value = place.properties.lon;

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

  /**
   * Configura el botón de toggle de contraseña para mostrar/ocultar
   * la contraseña en el formulario.
   * @param {HTMLFormElement} form - Formulario que contiene el botón y el input de la contraseña
   */
  function configurarTogglePassword(form) {
    const toggle = form.querySelector("#togglePassword");
    const icono = form.querySelector("#iconoPassword");
    const contrasena = form.querySelector("#contrasena");
  
    if (!toggle || !icono || !contrasena) return;
  
    toggle.addEventListener("click", () => {
      const isPassword = contrasena.type === "password";
      contrasena.type = isPassword ? "text" : "password";
      icono.classList.toggle("bi-eye");
      icono.classList.toggle("bi-eye-slash");
    });
  }
  

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
      repetirContrasena: formActivo.querySelector("#repetirContrasena"),
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

      if (key === "contrasena" && !/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[\W_]).{8,}$/.test(valor)) {
        input.classList.add("is-invalid");
        input.focus();
        valido = false;
        break;
      }

      if (key === "repetirContrasena" && campos.contrasena.value !== valor) {
        inputRepetir.classList.add("is-invalid");
        inputRepetir.focus();
        valido = false;
        break;
      }

      if (key === "telefono" && !/^\d{9}$/.test(valor)) {
        input.classList.add("is-invalid");
        input.focus();
        valido = false;
        break;
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
      formActivo.requestSubmit();
    }
  });
});
