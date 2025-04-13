document.addEventListener("DOMContentLoaded", function () {
  const apiKey = "eddca3679eed46f79e25f3aa58c75054";

  const formUsuario = document.getElementById("form-usuario");
  const formRestaurante = document.getElementById("form-restaurante");
  const containerUsuario = document.querySelector(".container-usuario");
  const containerRestaurante = document.querySelector(".container-restaurante");
  const checkbox = document.getElementById("activarRestaurante");
  const btnRegistro = document.getElementById("btn-registro");

  let timeout;

  /**
   * Sincroniza los campos de un formulario origen con los campos
   * de un formulario destino. La sincronizaci n se hace por coincidencia
   * de sufijo en el nombre del campo. Si el nombre del campo del formulario
   * origen comienza por "propietario." o "direccionRestaurante.", se
   * quita ese prefijo antes de buscar el campo en el formulario destino.
   * @param {HTMLFormElement} origen Formulario origen que contiene los
   *   campos que se van a sincronizar.
   * @param {HTMLFormElement} destino Formulario destino que contendr  los
   *   campos sincronizados.
   */
  function sincronizarCampos(origen, destino) {
    const inputsOrigen = origen.querySelectorAll("input, textarea");

    inputsOrigen.forEach(inputOrigen => {
      const name = inputOrigen.name;
      if (!name) return;

      // Normaliza el nombre del campo (quita propietario. o direccionRestaurante.)
      const nombreBase = name.replace(/^propietario\.|^direccionRestaurante\./, "");

      // Busca en destino por coincidencia de sufijo
      const inputDestino = destino.querySelector(`[name$=".${nombreBase}"]`);
      if (inputDestino) {
        inputDestino.value = inputOrigen.value;
      }
    });
  }

  /**
   * Cambia el formulario visible en la pantalla de registro.
   * Si esRestaurante es true, se muestra el formulario de restaurante.
   * Si esRestaurante es false, se muestra el formulario de usuario.
   * Antes de mostrar el formulario, se guardan los campos de entrada
   * del formulario actual y se restauran los campos de entrada
   * del formulario que se va a mostrar.
   * @param {boolean} esRestaurante Indica si se debe mostrar el formulario
   *   de restaurante o de usuario.
   */
  function toggleFormularios(esRestaurante) {
    formUsuario.classList.remove("mostrar");
    formRestaurante.classList.remove("mostrar");
    containerUsuario.classList.remove("mostrar");
    containerRestaurante.classList.remove("mostrar");
  
    const acordeonContenido = document.getElementById("contenido-acordeon-restaurante");
    const botonAcordeon = document.querySelector(".accordion-button");
  
    if (esRestaurante) {
      sincronizarCampos(formUsuario, formRestaurante);
  
      formRestaurante.classList.add("mostrar");
      containerRestaurante.classList.add("mostrar");
  
      new bootstrap.Collapse(acordeonContenido, { toggle: false }).show();
      botonAcordeon.classList.add("disabled");
      botonAcordeon.setAttribute("aria-disabled", "true");
      botonAcordeon.style.pointerEvents = "none";
    } else {
      sincronizarCampos(formRestaurante, formUsuario);
  
      formUsuario.classList.add("mostrar");
      containerUsuario.classList.add("mostrar");
  
      botonAcordeon.classList.remove("disabled");
      botonAcordeon.removeAttribute("aria-disabled");
      botonAcordeon.style.pointerEvents = "auto";
  
      new bootstrap.Collapse(acordeonContenido, { toggle: false }).hide();
    }
  
    const formActivo = esRestaurante ? formRestaurante : formUsuario;
    configurarTogglePassword(formActivo);
  }  
  
  /**
   * Configura el botón de toggle de visibilidad de contraseña en un formulario.
   * Busca un botón con id "togglePassword" en el formulario y lo reemplaza
   * con un clon. Luego agrega un listener de click al botón clonado que
   * alterna entre mostrar y ocultar la contraseña.
   * @param {HTMLFormElement} form El formulario que contiene el botón de toggle.
   */
  function configurarTogglePassword(form) {
    const toggle = form.querySelector("#togglePassword");
    const icono = form.querySelector("#iconoPassword");
    const password = form.querySelector('[name$="password"]');
  
    if (!toggle || !icono || !password) return;
  
    if (toggle.dataset.listener === "true") return;
    toggle.dataset.listener = "true";
  
    toggle.addEventListener("click", () => {
      const isPassword = password.type === "password";
      password.type = isPassword ? "text" : "password";
      icono.classList.toggle("bi-eye");
      icono.classList.toggle("bi-eye-slash");
    });
  }   
  
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
   * @param {HTMLDivElement} dropdown - Contenedor del dropdown
   * @param {HTMLFormElement} form - Formulario activo
   */
  function mostrarSugerencias(features, prefix, dropdown, form) {
    features.forEach(place => {
      const item = document.createElement("div");
      item.className = "autocomplete-item";
      item.textContent = place.properties.formatted;
      
      item.addEventListener("click", () => {
        form.querySelector(`[name="${prefix}.calle"]`).value = place.properties.street || place.properties.address_line1 || '';
        form.querySelector(`[name="${prefix}.numCalle"]`).value = place.properties.housenumber || '';
        form.querySelector(`[name="${prefix}.ciudad"]`).value = place.properties.city || '';
        form.querySelector(`[name="${prefix}.provincia"]`).value = place.properties.county || place.properties.city;
        form.querySelector(`[name="${prefix}.codigoPostal"]`).value = place.properties.postcode || '';
        form.querySelector(`[name="${prefix}.latitud"]`).value = place.properties.lat || '';
        form.querySelector(`[name="${prefix}.longitud"]`).value = place.properties.lon || '';
        dropdown.innerHTML = "";
      });
      
      dropdown.appendChild(item);
    });
  }  
  
  /**
   * Configura el autocompletado de dirección en un formulario con base en el prefix dado
   * @param {HTMLFormElement} form - Formulario activo
   * @param {string} prefix - Nombre del atributo (ej. 'direccion' o 'direccionRestaurante')
  */
 function configurarAutocompletadoDireccion(form, prefix) {
    const inputCalle = form.querySelector(`[name="${prefix}.calle"]`);
    const dropdown = form.querySelector(`[data-dropdown="${prefix}"]`);
  
    if (!inputCalle || !dropdown) return;
    
    inputCalle.addEventListener("input", () => {
      const query = inputCalle.value.trim();
      clearTimeout(timeout);
  
      if (query.length < 3) {
        dropdown.innerHTML = "";
        return;
      }
  
      timeout = setTimeout(() => {
        fetch(`https://api.geoapify.com/v1/geocode/autocomplete?text=${encodeURIComponent(query)}&lang=es&filter=countrycode:es&apiKey=${apiKey}`)
          .then(res => res.json())
          .then(data => {
            dropdown.innerHTML = "";
            if (!data.features) return;
            mostrarSugerencias(data.features, prefix, dropdown, form);
          })
          .catch(err => console.error("Error al obtener sugerencias:", err));
      }, 300);
    });
  }     
  
  configurarAutocompletadoDireccion(formUsuario, "direccion");
  configurarAutocompletadoDireccion(formRestaurante, "propietario.direccion");
  configurarAutocompletadoDireccion(formRestaurante, "direccionRestaurante");
  toggleFormularios(checkbox.checked);
  configurarTogglePassword(checkbox.checked ? formRestaurante : formUsuario);

  checkbox.addEventListener("change", () => {
    toggleFormularios(checkbox.checked);
  });
  
  document.addEventListener("click", function (e) {
    if (!e.target.closest(".input-autocomplete-wrapper")) {
      document.querySelectorAll(".autocomplete-dropdown").forEach(dropdown => {
        dropdown.innerHTML = "";
      });
    }
  });  
  
  btnRegistro?.addEventListener("click", () => {
    const formActivo = document.querySelector(".form-registro.mostrar");
    if (!formActivo) return;

    let valido = true;
    const prefix = formActivo.id === "form-restaurante" ? "propietario" : "";
    const direccionPrefix = formActivo.id === "form-restaurante" ? "direccionRestaurante" : "direccion";

    const campos = {
      nombre: formActivo.querySelector(`[name$="${prefix ? prefix + ".nombre" : "nombre"}"]`),
      apellidos: formActivo.querySelector(`[name$="${prefix ? prefix + ".apellidos" : "apellidos"}"]`),
      username: formActivo.querySelector(`[name$="${prefix ? prefix + ".username" : "username"}"]`),
      email: formActivo.querySelector(`[name$="${prefix ? prefix + ".email" : "email"}"]`),
      password: formActivo.querySelector(`[name$="${prefix ? prefix + ".password" : "password"}"]`),
      repeatPass: formActivo.querySelector("#repeatPass"),
      telefonoMovil: formActivo.querySelector(`[name$="${prefix ? prefix + ".telefonoMovil" : "telefonoMovil"}"]`),
      calle: formActivo.querySelector(`[name$="${direccionPrefix}.calle"]`),
      ciudad: formActivo.querySelector(`[name$="${direccionPrefix}.ciudad"]`),
      provincia: formActivo.querySelector(`[name$="${direccionPrefix}.provincia"]`),
      numCalle: formActivo.querySelector(`[name$="${direccionPrefix}.numCalle"]`),
      codigoPostal: formActivo.querySelector(`[name$="${direccionPrefix}.codigoPostal"]`)
    };

    Object.values(campos).forEach(input => input?.classList.remove("is-invalid"));

    for (const [key, input] of Object.entries(campos)) {
      if (!input) continue;
      const valor = input.value.trim();
    
      // Validación común: campo vacío
      if (!valor) {
        input.classList.add("is-invalid");
        input.focus();
        valido = false;
        break;
      }
    
      switch (key) {
        case "nombre":
          if (valor.length < 3 || valor.length > 20) {
            input.classList.add("is-invalid");
            input.focus();
            valido = false;
          }
          break;
    
        case "apellidos":
          if (valor.length < 6 || valor.length > 30) {
            input.classList.add("is-invalid");
            input.focus();
            valido = false;
          }
          break;
    
        case "username":
          if (valor.length < 6 || valor.length > 20) {
            input.classList.add("is-invalid");
            input.focus();
            valido = false;
          }
          break;
    
        case "email":
          if (!/^\S+@\S+\.\S+$/.test(valor)) {
            input.classList.add("is-invalid");
            input.focus();
            valido = false;
          }
          break;
    
        case "password":
          if (!/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[\W_]).{8,}$/.test(valor)) {
            input.classList.add("is-invalid");
            input.focus();
            valido = false;
          }
          break;
    
        case "repeatPass":
          if (valor !== campos.password.value) {
            input.classList.add("is-invalid");
            input.focus();
            valido = false;
          }
          break;
    
        case "telefonoMovil":
          if (!/^[67]\d{8}$/.test(valor)) {
            input.classList.add("is-invalid");
            input.focus();
            valido = false;
          }
          break;
      }
    
      if (!valido) break;
    }    

    if (valido && checkbox.checked) {
      const nombreCom = formActivo.querySelector('[name$="nombreComercial"]');
      const desc = formActivo.querySelector('[name$="descripcion"]');
      const telFijo = formActivo.querySelector('[name$="telefonoFijo"]');

      if (!nombreCom || nombreCom.value.trim().length < 6) {
        nombreCom.classList.add("is-invalid");
        nombreCom.focus();
        valido = false;
      } else if (!desc || desc.value.trim().length < 6) {
        desc.classList.add("is-invalid");
        desc.focus();
        valido = false;
      } else if (telFijo && telFijo.value && !/^[89]\d{8}$/.test(telFijo.value)) {
        telFijo.classList.add("is-invalid");
        telFijo.focus();
        valido = false;
      }
    }

    if (valido) {
      formActivo.requestSubmit();
    }
  });
});