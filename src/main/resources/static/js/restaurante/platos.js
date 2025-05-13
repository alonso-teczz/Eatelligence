document.addEventListener('DOMContentLoaded', function(){
    const verifyBtn = document.getElementById('checkout-verify-btn');
    if (verifyBtn) {
      verifyBtn.addEventListener('click', function(){
        Swal.fire({
          title: 'Cuenta no verificada',
          text: 'Para finalizar tu compra debes verificar tu cuenta.',
          icon: 'warning',
          showCancelButton: true,
          confirmButtonText: 'Verificar cuenta',
          cancelButtonText: 'Cancelar',
          confirmButtonColor: '#ffc107',
          cancelButtonColor: '#6c757d'
        }).then(result => {
          if (result.isConfirmed) {
            window.location.href = '/pending-verification';
          }
        });
      });
    }
});  