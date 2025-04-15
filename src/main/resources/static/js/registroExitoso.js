document.addEventListener("DOMContentLoaded", () => {
  const body = document.body;

  const correoEmpresaFallido = body.dataset.correoempresafallido === "true";
  const correoPropietarioFallido = body.dataset.correopropietariofallido === "true";
  const email = body.dataset.email;
  const nombreRestaurante = body.dataset.nombrerestaurante;
  const token = body.dataset.token;

  const huboFallo = correoEmpresaFallido || correoPropietarioFallido;

  let titulo, icono, htmlMensaje;

  if (huboFallo) {
      titulo = '¡Cuenta creada, pero con avisos!';
      icono = 'warning';
      htmlMensaje = `<strong>${nombreRestaurante}</strong> y su propietario fueron registrados correctamente.<br><br>`;

      if (correoEmpresaFallido) {
          htmlMensaje += '❌ No se pudo enviar el correo de verificación a <code>email de empresa</code>.<br>';
      } else {
          htmlMensaje += '✅ Correo enviado correctamente al email de empresa.<br>';
      }

      if (correoPropietarioFallido) {
          htmlMensaje += `❌ No se pudo enviar el correo de verificación al propietario (<code>${email}</code>).<br>`;
      } else {
          htmlMensaje += `✅ Correo enviado correctamente al propietario (<code>${email}</code>).<br>`;
      }

  } else {
      titulo = '¡Registro exitoso!';
      icono = 'success';
      htmlMensaje = `Tu restaurante <strong>${nombreRestaurante}</strong> y su propietario fueron registrados correctamente.<br>Hemos enviado los correos de verificación.`;
  }

  Swal.fire({
      icon: icono,
      title: titulo,
      html: htmlMensaje,
      confirmButtonText: huboFallo ? 'Reenviar correo' : 'Aceptar',
      showCancelButton: huboFallo,
      cancelButtonText: 'Más tarde',
      confirmButtonColor: '#1d6448',
      cancelButtonColor: '#6c757d'
  }).then(result => {
    if (result.isConfirmed && huboFallo) {
        let tipo = correoEmpresaFallido ? "RESTAURANTE" : "USUARIO";

        fetch(`/reenviar-verificacion?token=${encodeURIComponent(token)}&tipo=${tipo}`, {
            method: 'POST'
        })
        .then(res => {
            if (res.ok) {
                Swal.fire({
                    icon: 'success',
                    title: 'Correo reenviado',
                    text: 'Hemos reenviado el correo de verificación correctamente.',
                    confirmButtonColor: '#1d6448'
                });
            } else {
                Swal.fire({
                    icon: 'error',
                    title: 'Error',
                    text: 'No se pudo reenviar el correo. Intenta más tarde.',
                    confirmButtonColor: '#1d6448'
                });
            }
        });
    }
  });
});