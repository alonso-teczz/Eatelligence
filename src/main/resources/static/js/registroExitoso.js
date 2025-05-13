document.addEventListener("DOMContentLoaded", () => {
  const body = document.body;
  const tiempoBloqueo = parseInt(body.dataset.tiempoBloqueo || "0");
  const correoRestFallido = body.dataset.correoRestFallido === "true";
  const correoUsuarioFallido = body.dataset.correoUsuarioFallido === "true";
  const email = body.dataset.email;
  const nombreRestaurante = body.dataset.nombreRestaurante;
  const token = body.dataset.token;
  const esRestaurante = !!nombreRestaurante;
  const emailError = correoRestFallido || correoUsuarioFallido;

  let titulo = '¡Registro exitoso!';
  let icono = 'success';
  let mensaje = '';

  if (correoRestFallido && correoUsuarioFallido) {
    mensaje = `El restaurante <strong>${nombreRestaurante}</strong> y su propietario se registraron correctamente, pero no pudimos enviar los correos de verificación.`;
  } else if (correoRestFallido) {
    mensaje = `El restaurante <strong>${nombreRestaurante}</strong> fue registrado correctamente. El correo de verificación al restaurante no se pudo enviar.`;
  } else if (correoUsuarioFallido) {
    mensaje = esRestaurante
      ? `El restaurante <strong>${nombreRestaurante}</strong> y su propietario fueron registrados correctamente. Sin embargo, no se pudo enviar el correo al propietario (<strong>${email}</strong>).`
      : `Tu cuenta fue registrada correctamente. Sin embargo, no se pudo enviar el correo a <strong>${email}</strong>.`;
  } else {
    mensaje = esRestaurante
      ? `Tu restaurante <strong>${nombreRestaurante}</strong> y su propietario fueron registrados correctamente.<br>Hemos enviado los correos de verificación.`
      : `Tu cuenta fue registrada correctamente.<br>Hemos enviado el correo de verificación a <strong>${email || 'tu dirección de correo'}</strong>.`;

  }

  Swal.fire({
    icon: icono,
    title: titulo,
    html: mensaje,
    confirmButtonText: 'Aceptar',
    confirmButtonColor: '#1d6448',
    allowOutsideClick: false,
    allowEscapeKey: false
  });

  const cuentaAtrasContainer = document.getElementById("cuentaAtrasContainer");
  const cuentaAtrasTexto = document.getElementById("cuentaAtrasTexto");
  const btnReenviar = document.getElementById("btn-reenviar");

  if (emailError && cuentaAtrasContainer && cuentaAtrasTexto && btnReenviar) {
    cuentaAtrasContainer.style.display = "block";

    let tiempoRestante = tiempoBloqueo;

  /**
   * Actualiza el texto de #cuentaAtrasTexto con el tiempo restante
   * para reenviar el correo.
   *
   * @private
   */
    const actualizarCuentaAtras = () => {
      const horas = Math.floor(tiempoRestante / 3600);
      const minutos = Math.floor((tiempoRestante % 3600) / 60);
      const segundos = tiempoRestante % 60;

      const tiempoFormateado =
        (horas > 0 ? `${horas}h ` : '') +
        (minutos > 0 ? `${minutos}m ` : '') +
        `${segundos < 10 ? '0' + segundos : segundos}s`;

      cuentaAtrasTexto.innerHTML = `Debes esperar <strong>${tiempoFormateado}</strong> para reenviar el correo.`;
    };

    actualizarCuentaAtras();

    const interval = setInterval(() => {
      tiempoRestante--;

      if (tiempoRestante <= 0) {
        clearInterval(interval);
        cuentaAtrasTexto.innerHTML = `<strong>¡Ya puedes reenviar el correo!</strong>`;
        btnReenviar.disabled = false;

        Swal.fire({
          toast: true,
          position: 'top-end',
          icon: 'info',
          title: 'Ya puedes reenviar el correo de verificación',
          showConfirmButton: false,
          timer: 3000,
          timerProgressBar: true,
          background: '#e0f0ff',
          color: '#0d6efd',
          customClass: {
            popup: 'border-0 shadow rounded-4 px-4 py-3',
            title: 'fw-semibold fs-6',
            icon: 'text-primary fs-5 me-2'
          },
          didOpen: (toast) => {
            toast.addEventListener('mouseenter', Swal.stopTimer);
            toast.addEventListener('mouseleave', Swal.resumeTimer);
          }
        });
        return;
      }

      actualizarCuentaAtras();
    }, 1000);

    btnReenviar.addEventListener("click", () => {
      const tipo = correoRestFallido ? "RESTAURANTE" : "USUARIO";

      btnReenviar.disabled = true;
      cuentaAtrasTexto.innerHTML = `<span class="text-info fw-semibold">Procesando solicitud...</span>`;

      const csrfToken = document.getElementById('csrfToken').value;
      const csrfHeader = document.getElementById('csrfHeader').value;

      fetch(`/resend-verification?token=${encodeURIComponent(token)}&tipo=${tipo}`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded',
          'X-Requested-With': 'XMLHttpRequest',
          [csrfHeader]: csrfToken
        }
      }).then(async res => {
        const mensaje = await res.text();

        if (res.ok) {
          Swal.fire({
            icon: 'success',
            title: 'Correo reenviado',
            text: mensaje,
            confirmButtonColor: '#1d6448'
          });
          btnReenviar.disabled = true;
          cuentaAtrasTexto.innerHTML = `<span class="text-success fw-semibold">Correo reenviado correctamente. Espera unos minutos antes de volver a intentarlo.</span>`;
        } else {
          Swal.fire({
            icon: 'error',
            title: 'No se pudo reenviar el correo',
            text: mensaje,
            confirmButtonColor: '#1d6448'
          });
          btnReenviar.disabled = false;
        }
      });
    });
  }
});