document.addEventListener("DOMContentLoaded", () => {
  const btnRest = document.getElementById("btn-reenviar-restaurante");
  const btnProp = document.getElementById("btn-reenviar-propietario");
  const btnUsuario = document.getElementById("btn-reenviar-usuario");

  const csrfToken = document.getElementById("csrfToken")?.value;
  const csrfHeader = document.getElementById("csrfHeader")?.value;

  function reenviarVerificacion(token, tipo, button) {
    button.disabled = true;

    fetch(`/resend-verification?token=${encodeURIComponent(token)}&tipo=${tipo}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded',
        'X-Requested-With': 'XMLHttpRequest',
        [csrfHeader]: csrfToken
      }
    }).then(async res => {
      const mensaje = await res.text();

      if (res.status === 200) {
        Swal.fire({
          icon: 'success',
          title: 'Correo reenviado',
          text: mensaje,
          confirmButtonColor: '#1d6448'
        });
        button.disabled = false;

      } else if (res.status === 429) {
        // Extraer tiempo en segundos del mensaje
        let segundos = 60;
        const matchMin = mensaje.match(/(\\d+)\\s+minuto/);
        const matchSeg = mensaje.match(/(\\d+)\\s+segundo/);

        if (matchMin) segundos = parseInt(matchMin[1]) * 60;
        else if (matchSeg) segundos = parseInt(matchSeg[1]);

        // Mostrar el toast con botón para cerrar
        Swal.fire({
          toast: true,
          position: 'top-end',
          icon: 'info',
          title: mensaje,
          showConfirmButton: true,
          confirmButtonText: '✖',
          customClass: {
            confirmButton: 'btn p-0 m-0 border-0 bg-transparent position-absolute top-0 end-0 mt-2 me-3 text-dark fs-5',
            popup: 'border-0 shadow rounded-4 px-4 py-3',
            title: 'fw-semibold fs-6'
          },
          showCloseButton: false,
          timer: undefined,
          timerProgressBar: false
        });              

        // Habilitar el botón tras el tiempo real indicado por el servidor
        setTimeout(() => {
          button.disabled = false;
        }, segundos * 1000);
      } else {
        Swal.fire({
          icon: 'error',
          title: 'No se pudo reenviar el correo',
          text: mensaje
        });
        button.disabled = false;
      }
    }).catch(() => {
      Swal.fire({
        icon: 'error',
        title: 'Error de red',
        text: 'No se pudo contactar con el servidor.'
      });
      button.disabled = false;
    });
  }

  if (btnRest) {
    btnRest.addEventListener("click", () =>
      reenviarVerificacion(btnRest.dataset.token, "RESTAURANTE", btnRest)
    );
  }
  if (btnProp) {
    btnProp.addEventListener("click", () =>
      reenviarVerificacion(btnProp.dataset.token, "USUARIO", btnProp)
    );
  }
  if (btnUsuario) {
    btnUsuario.addEventListener("click", () =>
      reenviarVerificacion(btnUsuario.dataset.token, "USUARIO", btnUsuario)
    );
  }
});