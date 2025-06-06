document.addEventListener("DOMContentLoaded", async function () {
  const apiKey = await fetch('/api/apikeys/geoapify', { headers: { "X-Requested-With": "XMLHttpRequest" }})
    .then(res => res.json())
    .then(data => data.apiKey);

  const formUsuario = document.getElementById("form-usuario");
  const formRestaurante = document.getElementById("form-restaurante");
  const containerUsuario = document.querySelector(".container-usuario");
  const containerRestaurante = document.querySelector(".container-restaurante");
  const checkbox = document.getElementById("activarRestaurante");
  const btnRegistro = document.getElementById("btn-registro");

  let timeout;
  let passwordVisible = false;

  /**
   * Sincroniza los campos de dos formularios: origen y destino.
   * - Los campos de dirección (calle, numCalle, ciudad, provincia, código postal) se sincronizan
   *   entre los prefijos "direccion." y "propietario.direccion.".
   * - El campo repeatPass se copia directo por ID.
   * - La visibilidad del campo de contraseña se sincroniza.
   * - El resto de campos se sincronizan eliminando el prefijo "propietario." si lo tienen.
   * @param {HTMLElement} origen - El formulario de origen.
   * @param {HTMLElement} destino - El formulario de destino.
   */
  function sincronizarCampos(origen, destino) {
    const camposDireccion = ["calle", "numCalle", "ciudad", "provincia", "codigoPostal"];
    const inputsOrigen = origen.querySelectorAll("input, textarea, select");
  
    inputsOrigen.forEach(inputOrigen => {
      const nameOrigen = inputOrigen.name;
      if (!nameOrigen) return;
  
      const valor = inputOrigen.value;   
  
      // Detectar si es campo de dirección
      const campoDireccion = camposDireccion.find(c => nameOrigen.endsWith("." + c));
      if (campoDireccion) {
        const nombreBase = nameOrigen.replace(/^direccion\.|^propietario\.direccion\./, "");
        const inputDestino = Array.from(destino.querySelectorAll("input"))
          .find(input => input.name.endsWith("." + nombreBase) && (input.name.startsWith("direccion.") || input.name.startsWith("propietario.direccion.")));
        if (inputDestino) {
          inputDestino.value = valor;
          copiarEstadoValidacion(inputOrigen, inputDestino);
        }
        return;
      }
  
      // Campos normales
      const nombreBase = nameOrigen.replace(/^propietario\./, "");
      const inputDestino = Array.from(destino.querySelectorAll("input, textarea"))
        .find(input => input.name === nombreBase || input.name.endsWith("." + nombreBase));
  
      if (inputDestino) {
        inputDestino.value = valor;
        copiarEstadoValidacion(inputOrigen, inputDestino);
      }
    });
  }
  
  
  /**
   * Copia el valor y el estado de validación de un campo de formulario a otro.
   * @param {HTMLElement} inputOrigen - El campo de origen.
   * @param {HTMLElement} inputDestino - El campo de destino.
   */
  function copiarEstadoValidacion(inputOrigen, inputDestino) {
    // Copiar clases de validación
    inputDestino.classList.remove("is-valid", "is-invalid");
    if (inputOrigen.classList.contains("is-valid")) inputDestino.classList.add("is-valid");
    if (inputOrigen.classList.contains("is-invalid")) inputDestino.classList.add("is-invalid");
  
    // Copiar contenido de feedback dinámico si tienen mismo ID base (por ejemplo: username-feedback-success)
    const feedbackTipos = ["success", "error"];
    feedbackTipos.forEach(tipo => {
      const idOrigen = inputOrigen.id ? `${inputOrigen.id}-feedback-${tipo}` : null;
      const idDestino = inputDestino.id ? `${inputDestino.id}-feedback-${tipo}` : null;
  
      if (idOrigen && idDestino) {
        const msgOrigen = document.getElementById(idOrigen);
        const msgDestino = document.getElementById(idDestino);
        if (msgOrigen && msgDestino) {
          msgDestino.innerText = msgOrigen.innerText;
          msgDestino.className = msgOrigen.className; // mantiene clases como d-none
        }
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
    const formularioVisible = formUsuario.classList.contains("mostrar") ? formUsuario : formRestaurante;
    const formularioOculto = esRestaurante ? formRestaurante : formUsuario;
  
    // Guardar el estado de visibilidad actual de la contraseña
    const passwordActual = formularioVisible.querySelector('[name$="password"]');
    if (passwordActual) {
      passwordVisible = passwordActual.type === "text";
    }
  
    sincronizarCampos(formularioVisible, formularioOculto);
  
    formUsuario.classList.remove("mostrar");
    formRestaurante.classList.remove("mostrar");
    containerUsuario.classList.remove("mostrar");
    containerRestaurante.classList.remove("mostrar");
  
    const acordeonContenido = document.getElementById("contenido-acordeon-restaurante");
    const botonAcordeon = document.querySelector(".accordion-button");
  
    if (esRestaurante) {
      formRestaurante.classList.add("mostrar");
      containerRestaurante.classList.add("mostrar");
  
      new bootstrap.Collapse(acordeonContenido, { toggle: false }).show();
      botonAcordeon.classList.add("disabled");
      botonAcordeon.setAttribute("aria-disabled", "true");
      botonAcordeon.style.pointerEvents = "none";
  
      configurarTogglePassword(formRestaurante);
      aplicarVisibilidadPassword(formRestaurante);
    } else {
      formUsuario.classList.add("mostrar");
      containerUsuario.classList.add("mostrar");
  
      botonAcordeon.classList.remove("disabled");
      botonAcordeon.removeAttribute("aria-disabled");
      botonAcordeon.style.pointerEvents = "auto";
  
      new bootstrap.Collapse(acordeonContenido, { toggle: false }).hide();
  
      configurarTogglePassword(formUsuario);
      aplicarVisibilidadPassword(formUsuario);
    }
  }
  
  /**
   * Aplica la visibilidad de la contraseña en un formulario.
   * Busca el campo de contraseña y el icono de toggle de visibilidad
   * en el formulario y aplica la visibilidad actual de la contraseña.
   * Si passwordVisible es true, muestra la contraseña como texto,
   * de lo contrario la oculta.
   * @param {HTMLFormElement} form Formulario que contiene el campo de contraseña
   *   y el icono de toggle de visibilidad.
   */
  function aplicarVisibilidadPassword(form) {
    const password = form.querySelector('[name$="password"]');
    const icono = form.querySelector("#iconoPassword");
    if (!password || !icono) return;
  
    password.type = passwordVisible ? "text" : "password";
  
    icono.classList.remove("bi-eye", "bi-eye-slash");
    icono.classList.add(passwordVisible ? "bi-eye-slash" : "bi-eye");
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

  /**
   * Configura la comprobación en vivo de la disponibilidad de un username en los
   * formularios de registro de usuario y de propietario de restaurante.
   *
   * Escucha el evento "input" en los campos de texto con id "username" y
   * "propietario.username", y muestra un feedback visual según si el username
   * ya existe o no. Si el username tiene menos de 6 caracteres, no se muestra
   * ningún feedback.
   */
  function configurarComprobacionUsername() {
    const inputs = [document.getElementById("username"), document.getElementById("propietario.username")];
  
    inputs.forEach(input => {
      if (!input) return;
  
      input.addEventListener("input", () => {
        const username = input.value.trim();
  
        const successFeedback = document.getElementById(`${input.id}-feedback-success`);
        const errorFeedback = document.getElementById(`${input.id}-feedback-error`);
  
        // Ocultar feedback si el username es demasiado corto
        if (username.length < 6) {
          ocultarFeedback(input, successFeedback, errorFeedback);
          return;
        }
  
        fetch(`/api/users/exists?username=${encodeURIComponent(username)}`, {
          headers: {
            "X-Requested-With": "XMLHttpRequest"
          }
        })
          .then(res => res.json())
          .then(existe => {
            if (existe) {
              mostrarError(input, errorFeedback, successFeedback);
            } else {
              mostrarSuccess(input, successFeedback, errorFeedback);
            }
          })
          .catch(err => {
            console.error("Error al verificar username", err);
            ocultarFeedback(input, successFeedback, errorFeedback);
          });
      });
    });
  
    /**
     * Muestra un feedback de error en el input y oculta el feedback de éxito.
     * Agrega la clase "is-invalid" al input y la clase "d-none" al feedback de éxito.
     * Quita la clase "is-valid" del input y la clase "d-none" del feedback de error.
     * Si hay un feedback estático con clase "validation-static", lo oculta.
     * @param {HTMLInputElement} input - El input a mostrar el feedback
     * @param {HTMLElement} [error] - El elemento de feedback de error
     * @param {HTMLElement} [success] - El elemento de feedback de éxito
     */
    function mostrarError(input, error, success) {
      input.classList.add("is-invalid");
      input.classList.remove("is-valid");
      if (error) error.classList.remove("d-none");
      if (success) success.classList.add("d-none");
    
      // Oculta feedback estático para evitar duplicado
      const staticMsgErr = input.parentElement.querySelector(".validation-static");
      if (staticMsgErr) staticMsgErr.classList.add("d-none");
    }
    
    /**
     * Muestra el feedback de éxito para el input dado
     * @param {HTMLInputElement} input - Campo de texto que ha superado la validación
     * @param {HTMLElement} [success] - Elemento que contiene el mensaje de éxito
     * @param {HTMLElement} [error] - Elemento que contiene el mensaje de error
     */
    function mostrarSuccess(input, success, error) {
      input.classList.remove("is-invalid");
      input.classList.add("is-valid");
      if (success) success.classList.remove("d-none");
      if (error) error.classList.add("d-none");
    
      const staticMsgErr = input.parentElement.querySelector(".validation-static");
      if (staticMsgErr) staticMsgErr.classList.add("d-none");
    }
    
  /**
   * Oculta el feedback de validación para el input dado.
   * Quita las clases "is-valid" y "is-invalid" del input, y muestra el feedback
   * estático con clase "validation-static" si existe. Si se proporcionan los
   * elementos de feedback de éxito y error, los oculta.
   * @param {HTMLInputElement} input - Campo de texto que se va a ocultar el feedback
   * @param {HTMLElement} [success] - Elemento que contiene el mensaje de éxito
   * @param {HTMLElement} [error] - Elemento que contiene el mensaje de error
   */
    function ocultarFeedback(input, success, error) {
      input.classList.remove("is-valid", "is-invalid");
      if (success) success.classList.add("d-none");
      if (error) error.classList.add("d-none");
    
      const staticMsgErr = input.parentElement.querySelector(".validation-static");
      if (staticMsgErr) staticMsgErr.classList.remove("d-none");
    }    
  }  
  
  configurarAutocompletadoDireccion(formUsuario, "direccion");
  configurarAutocompletadoDireccion(formRestaurante, "propietario.direccion");
  configurarAutocompletadoDireccion(formRestaurante, "direccionRestaurante");
  toggleFormularios(checkbox.checked);
  configurarTogglePassword(checkbox.checked ? formRestaurante : formUsuario);
  configurarComprobacionUsername();

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
  
  btnRegistro?.addEventListener("click", async () => {
    const formActivo = document.querySelector(".form-registro.mostrar");
    if (!formActivo) return;
  
    let valido = true;
    const esRestaurante = formActivo.id === "form-restaurante";
    const prefix = esRestaurante ? "propietario" : "";
    const direccionPrefix = esRestaurante ? "propietario.direccion" : "direccion";
  
    const camposUsuario = {
      nombre: formActivo.querySelector(`[name$="${prefix ? prefix + ".nombre" : "nombre"}"]`),
      apellidos: formActivo.querySelector(`[name$="${prefix ? prefix + ".apellidos" : "apellidos"}"]`),
      username: formActivo.querySelector(`[name$="${prefix ? prefix + ".username" : "username"}"]`),
      email: formActivo.querySelector(`[name$="${prefix ? prefix + ".email" : "email"}"]`),
      password: formActivo.querySelector(`[name$="${prefix ? prefix + ".password" : "password"}"]`),
      repeatPass: formActivo.querySelector(`[name$="repeatPass"]`),
      telefonoMovil: formActivo.querySelector(`[name$="${prefix ? prefix + ".telefonoMovil" : "telefonoMovil"}"]`),
      calle: formActivo.querySelector(`[name$="${direccionPrefix}.calle"]`),
      ciudad: formActivo.querySelector(`[name$="${direccionPrefix}.ciudad"]`),
      provincia: formActivo.querySelector(`[name$="${direccionPrefix}.provincia"]`),
      numCalle: formActivo.querySelector(`[name$="${direccionPrefix}.numCalle"]`),
      codigoPostal: formActivo.querySelector(`[name$="${direccionPrefix}.codigoPostal"]`)
    };

    const direccionRestaurante = {
      calle: formRestaurante.querySelector('[name="direccionRestaurante.calle"]'),
      ciudad: formRestaurante.querySelector('[name="direccionRestaurante.ciudad"]'),
      provincia: formRestaurante.querySelector('[name="direccionRestaurante.provincia"]'),
      numCalle: formRestaurante.querySelector('[name="direccionRestaurante.numCalle"]'),
      codigoPostal: formRestaurante.querySelector('[name="direccionRestaurante.codigoPostal"]')
    };

    [...Object.values(camposUsuario), ...Object.values(direccionRestaurante)].forEach(input => input?.classList.remove("is-invalid"));
    
    const globalError = document.querySelector('#global-error') || null;
    
    if (globalError) {
      globalError.style.display = 'none';
    }

    // Validar campos principales
    for (const [key, input] of Object.entries(camposUsuario)) {
      if (!input) continue;
      const valor = input.value.trim();
      
      if (!valor) {
        const isDirectionField = ["calle", "ciudad", "provincia", "numCalle", "codigoPostal"].includes(key);
    
        // Validación especial si es formulario de restaurante y el campo es de dirección del propietario
        if (esRestaurante && isDirectionField) {
          const camposDireccion = ["calle", "ciudad", "provincia", "numCalle", "codigoPostal"];
          const someFilled = camposDireccion.some(campo => camposUsuario[campo]?.value.trim() !== "");
          const allFilled = camposDireccion.every(campo => camposUsuario[campo]?.value.trim() !== "");
    
          if (someFilled && !allFilled) {
            input.classList.add("is-invalid");
            input.focus();
            valido = false;
            break;
          }
    
          continue;
        }
    
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
          } else {
            try {
              const existe = await fetch(`/api/users/exists?username=${encodeURIComponent(username)}`, {
                headers: {
                  "X-Requested-With": "XMLHttpRequest"
                }
              })
                .then(res => res.json());
        
              if (existe) {
                input.classList.add("is-invalid");
                input.focus();
        
                const feedback = document.getElementById(`${input.id}-feedback-error`);
                if (feedback) {
                  feedback.classList.remove("d-none");
                }
        
                valido = false;
              }
            } catch (err) {
              console.error("Error al validar el nombre de usuario:", err);
              valido = false;
            }
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
          if (valor !== camposUsuario.password.value) {
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
      const camposRestaurante = {
        nombreComercial: formActivo.querySelector('[name="nombreComercial"]'),
        descripcion: formActivo.querySelector('[name="descripcion"]'),
        emailEmpresa: formActivo.querySelector('[name="emailEmpresa"]'),
        telFijo: formActivo.querySelector('[name="telefonoFijo"]')
      };

      const { nombreComercial, descripcion, emailEmpresa, telFijo } = camposRestaurante;

      [...Object.values(camposRestaurante)].forEach(input => input?.classList.remove("is-invalid"));
  
      if (!nombreComercial || nombreComercial.value.trim().length < 6) {
        nombreComercial.classList.add("is-invalid");
        nombreComercial.focus();
        valido = false;
      } else if (!descripcion || descripcion.value.trim().length < 6) {
        descripcion.classList.add("is-invalid");
        descripcion.focus();
        valido = false;
      } else if (!emailEmpresa || !/^\S+@\S+\.\S+$/.test(emailEmpresa.value.trim())) {
        emailEmpresa.classList.add("is-invalid");
        emailEmpresa.focus();
        valido = false;
      } else if (!telFijo && telFijo.value && !/^[89]\d{8}$/.test(telFijo.value)) {
        telFijo.classList.add("is-invalid");
        telFijo.focus();
        valido = false;
      }
    }

    // Validar dirección del restaurante
    if (valido && checkbox.checked) {
      for (const input of Object.values(direccionRestaurante)) {
        if (!input) continue;
        const valor = input.value.trim();
        if (!valor) {
          input.classList.add("is-invalid");
          input.focus();
          valido = false;
          break;
        }
      }
    }
  
    if (valido) {
      formActivo.requestSubmit();
    }
  });  
});