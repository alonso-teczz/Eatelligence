document.addEventListener('DOMContentLoaded', function() {
    // Obtener todas las tarjetas de métodos de pago
    const paymentCards = document.querySelectorAll('.payment-method-card');
  
    // Función para manejar la selección de método de pago
    function togglePaymentFields(event) {
      const selectedMethod = event.currentTarget.getAttribute('data-method');
      
      // Quitar la clase 'active' de todas las tarjetas
      paymentCards.forEach(card => card.classList.remove('active'));
      
      // Añadir la clase 'active' a la tarjeta seleccionada
      event.currentTarget.classList.add('active');
      
      // Ocultar todos los campos
      const fields = document.querySelectorAll('.payment-method-fields');
      fields.forEach(field => field.style.display = 'none');
  
      // Mostrar los campos correspondientes al método seleccionado
      const selectedFields = document.getElementById(selectedMethod + 'Fields');
      if (selectedFields) {
        selectedFields.style.display = 'block';
      }
    }
  
    // Asignar el event listener a cada tarjeta de pago
    paymentCards.forEach(card => {
      card.addEventListener('click', togglePaymentFields);
    });
  
    // Llamar a la función para activar la tarjeta por defecto si ya hay alguna seleccionada
    const activeCard = document.querySelector('.payment-method-card.active');
    if (activeCard) {
      togglePaymentFields({ currentTarget: activeCard });
    }
  });
  