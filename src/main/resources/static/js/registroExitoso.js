document.addEventListener("DOMContentLoaded", () => {
  const body = document.body;

  const tiempoBloqueo = parseInt(body.dataset.tiempobloqueo || "0");

  const correoEmpresaFallido = body.dataset.correoempresafallido === "true";
  const correoPropietarioFallido = body.dataset.correopropietariofallido === "true";
  const email = body.dataset.email;
  const nombreRestaurante = body.dataset.nombrerestaurante;
  const token = body.dataset.token;
  const esRestaurante = !!nombreRestaurante;
  const emailError = correoEmpresaFallido || correoPropietarioFallido;

  let titulo = '¡Registro exitoso!';
  let icono = 'success';
  let mensaje = esRestaurante
    ? `Tu restaurante <strong>${nombreRestaurante}</strong> y su propietario fueron registrados correctamente.<br>Hemos enviado los correos de verificación.`
    : `Tu cuenta fue registrada correctamente.<br>Hemos enviado el correo de verificación a <strong>${email}</strong>.`;

  const renderCuentaAtras = (restanteSegundos) => {
    const horas = Math.floor(restanteSegundos / 3600);
    const minutos = Math.floor((restanteSegundos % 3600) / 60);
    const segundos = restanteSegundos % 60;

    const tiempoFormateado =
      (horas > 0 ? `${horas}h ` : '') +
      (minutos > 0 ? `${minutos}m ` : '') +
      `${segundos < 10 ? '0' + segundos : segundos}s`;

    return `<br><br>Debes esperar <strong>${tiempoFormateado}</strong> para poder reenviar otro correo.`;
  };

  Swal.fire({
    icon: icono,
    title: titulo,
    html: mensaje + (tiempoBloqueo > 0 ? renderCuentaAtras(tiempoBloqueo) : ''),
    confirmButtonText: emailError ? 'Reenviar correo' : 'Aceptar',
    showCancelButton: emailError,
    cancelButtonText: 'Más tarde',
    confirmButtonColor: '#1d6448',
    cancelButtonColor: '#6c757d',
    allowOutsideClick: false,
    allowEscapeKey: false,
    customClass: { popup: 'swal-wide' },
    didOpen: () => {
      if (tiempoBloqueo > 0) {
        let tiempoRestante = tiempoBloqueo;

        const cuentaAtras = setInterval(() => {
          if (tiempoRestante <= 0) {
            clearInterval(cuentaAtras);
            Swal.update({
              html: mensaje + `<br><br><strong>¡Ya puedes reenviar el correo!</strong>`
            });
            return;
          }

          Swal.update({
            html: mensaje + renderCuentaAtras(tiempoRestante)
          });

          tiempoRestante--;
        }, 1000);
      }
    }
  }).then(result => {
    if (result.isConfirmed && emailError) {
      const tipo = correoEmpresaFallido ? "RESTAURANTE" : "USUARIO";

      fetch(`/reenviar-verificacion?token=${encodeURIComponent(token)}&tipo=${tipo}`, {
          method: 'POST',
          headers: {
            'X-Requested-With': 'XMLHttpRequest'
          }
        })
        .then(async res => {
        const mensaje = await res.text();

        if (res.ok) {
          Swal.fire({
            icon: 'success',
            title: 'Correo reenviado',
            text: mensaje,
            confirmButtonColor: '#1d6448',
            customClass: { popup: 'swal-wide' }
          });
        } else if (mensaje.includes("|")) {
          const [texto, segundos] = mensaje.split("|");
          let tiempoRestante = parseInt(segundos);

          Swal.fire({
            icon: 'info',
            title: 'Reenvío bloqueado',
            html: texto + renderCuentaAtras(tiempoRestante),
            confirmButtonText: 'Intentar reenviar',
            confirmButtonColor: '#1d6448',
            showConfirmButton: true,
            allowOutsideClick: false,
            allowEscapeKey: false,
            customClass: { popup: 'swal-wide' },
            didOpen: () => {
              const confirmBtn = Swal.getConfirmButton();
              confirmBtn.disabled = true;

              const interval = setInterval(() => {
                if (tiempoRestante <= 0) {
                  clearInterval(interval);
                  confirmBtn.disabled = false;
                  Swal.update({
                    html: `${texto}<br><br><strong>¡Ya puedes volver a intentarlo!</strong>`
                  });
                  return;
                }

                Swal.update({
                  html: texto + renderCuentaAtras(tiempoRestante)
                });

                tiempoRestante--;
              }, 1000);
            }
          });
        } else {
          Swal.fire({
            icon: 'error',
            title: 'No se pudo reenviar el correo',
            text: mensaje,
            confirmButtonColor: '#1d6448',
            customClass: { popup: 'swal-wide' }
          });
        }
      });
    }
  });
});
