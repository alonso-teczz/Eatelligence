import React, { useState, useEffect } from 'react';

function AutocompleteDireccion() {
  const [query, setQuery] = useState('');
  const [sugerencias, setSugerencias] = useState([]);
  const [ciudad, setCiudad] = useState('');
  const [provincia, setProvincia] = useState('');
  const [codigoPostal, setCodigoPostal] = useState('');

  const apiKey = import.meta.env.VITE_LOCATIONIQ_API_KEY;

  useEffect(() => {
    if (query.length < 4) {
      setSugerencias([]);
      return;
    }

    const timeout = setTimeout(() => {
      fetch(`https://api.locationiq.com/v1/autocomplete?key=${apiKey}&q=${encodeURIComponent(query)}&format=json`)
        .then((res) => res.json())
        .then((data) => {
          if (Array.isArray(data)) {
            setSugerencias(data);
          }
        })
        .catch((err) => {
          console.error('Error al obtener sugerencias:', err);
          setSugerencias([]);
        });
    }, 300);

    return () => clearTimeout(timeout);
  }, [query, apiKey]);

  const seleccionarSugerencia = (place) => {
    setQuery(place.display_name);
    const address = place.address || {};
    setCiudad(address.city || address.town || address.village || '');
    setProvincia(address.state || '');
    setCodigoPostal(address.postcode || '');
    setSugerencias([]);
  };

  return (
    <div className="mb-4">
      <label htmlFor="calle" className="form-label">Calle</label>
      <div className="position-relative">
        <input
          type="text"
          id="calle"
          className="form-control"
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          autoComplete="off"
          required
        />
        {sugerencias.length > 0 && (
          <div
          className="list-group position-absolute w-100 z-3 shadow"
          style={{
            maxHeight: '165px',
            overflowY: 'auto',
            overflowX: 'hidden',
            top: '100%',
            left: 0,
            right: 0,
            borderRadius: '0.5rem'
          }}
        >
            {sugerencias.map((place, index) => (
              <button
                key={index}
                className="list-group-item list-group-item-action"
                onClick={() => seleccionarSugerencia(place)}
                type="button"
              >
                {place.display_name}
              </button>
            ))}
          </div>
        )}
      </div>

      <div className="row mt-3">
        <div className="col-md-4">
          <label htmlFor="ciudad" className="form-label">Ciudad</label>
          <input type="text" id="ciudad" className="form-control" value={ciudad} readOnly />
        </div>
        <div className="col-md-4">
          <label htmlFor="provincia" className="form-label">Provincia</label>
          <input type="text" id="provincia" className="form-control" value={provincia} readOnly />
        </div>
        <div className="col-md-4">
          <label htmlFor="codigoPostal" className="form-label">Código Postal</label>
          <input type="text" id="codigoPostal" className="form-control" value={codigoPostal} readOnly />
        </div>
      </div>
    </div>
  );
}

export default AutocompleteDireccion;