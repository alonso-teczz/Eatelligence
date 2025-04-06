import React from 'react';
import { createRoot } from 'react-dom/client';
import AutocompleteDireccion from './components/AutocompleteDireccion';

const container = document.getElementById('react-autocompletador');
if (container) {
  const root = createRoot(container);
  root.render(<AutocompleteDireccion />);
}
else {
  console.error('No se encontró el contenedor para el autocompletador de dirección.');
}