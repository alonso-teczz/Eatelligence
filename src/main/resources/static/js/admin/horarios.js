document.addEventListener('DOMContentLoaded', () => {
    /* ──────────── Referencias DOM ──────────── */
    const tbody         = document.querySelector('#tabla-horarios tbody');
    const btnGuardar    = document.getElementById('btn-guardar');
    const modalEl       = document.getElementById('modalHorario');
    const modal         = new bootstrap.Modal(modalEl);
    const inputApertura = document.getElementById('input-apertura');
    const inputCierre   = document.getElementById('input-cierre');
    const chkTodos      = document.getElementById('chk-aplicar-todos');

    /* ──────────── Constantes útiles ──────────── */
    const EN = ['MONDAY','TUESDAY','WEDNESDAY','THURSDAY','FRIDAY','SATURDAY','SUNDAY'];
    const ES = ['Lunes','Martes','Miércoles','Jueves','Viernes','Sábado','Domingo'];

    /* ──────────── Estado in-memory ──────────── */
    let horarios = {};      // { MONDAY: [ {apertura, cierre}, … ], … }
    let diaEdit   = null;    // día que abrimos en modal
    let idxEdit   = -1;      // índice de la franja en ese día

    /* ──────────── Carga inicial desde API ──────────── */
    fetch('/api/restaurants/schedule', { headers: { 'X-Requested-With': 'XMLHttpRequest' } })
        .then(r => r.json())
        .then(({ data }) => {
            // montamos el objeto horarios
            data.forEach(h => {
                if (!horarios[h.dia]) horarios[h.dia] = [];
                horarios[h.dia].push({ apertura: h.apertura, cierre: h.cierre });
            });
            renderTable();
        });

    /* ──────────── Render de la tabla ──────────── */
    function renderTable() {
        tbody.innerHTML = '';
        EN.forEach((en, i) => {
            const slots = horarios[en] || [];

            if (slots.length === 0) {
                // fila vacía: SIN franjas
                tbody.insertAdjacentHTML('beforeend', `
                    <tr>
                      <td>${ES[i]}</td>
                      <td>--</td>
                      <td>--</td>
                      <td class="text-center">
                        <button class="btn btn-sm btn-primary" data-dia="${en}" data-idx="0">
                          <i class="fas fa-pen"></i>
                        </button>
                      </td>
                      <td class="text-center">
                        <button class="btn btn-sm btn-danger" data-remove-dia="${en}" data-remove-idx="0">
                          <i class="fas fa-trash"></i>
                        </button>
                      </td>
                    </tr>
                `);
            } else {
                // una fila por cada franja
                slots.forEach((s, j) => {
                    // día o icono ℹ️
                    const diaCell = j === 0
                        ? ES[i]
                        : `<td class="text-center">
                             <i class="fas fa-info-circle text-info ms-2"
                                data-bs-toggle="tooltip"
                                data-bs-placement="right"
                                title="Esta fila permite configurar un turno adicional para ${ES[i]}.">
                             </i>
                           </td>`;

                    // si es j=0, ya ponemos el <td> en la plantilla; si no, lo generamos arriba
                    if (j === 0) {
                        tbody.insertAdjacentHTML('beforeend', `
                          <tr>
                            <td>${ES[i]}</td>
                            <td>${s.apertura}</td>
                            <td>${s.cierre}</td>
                            <td class="text-center">
                              <button class="btn btn-sm btn-primary" data-dia="${en}" data-idx="${j}">
                                <i class="fas fa-pen"></i>
                              </button>
                            </td>
                            <td class="text-center">
                              <button class="btn btn-sm btn-danger" data-remove-dia="${en}" data-remove-idx="${j}">
                                <i class="fas fa-trash"></i>
                              </button>
                            </td>
                          </tr>
                        `);
                    } else {
                        // fila secundaria: incluimos celda vacía para día + icono
                        tbody.insertAdjacentHTML('beforeend', `
                          <tr>
                            ${diaCell}
                            <td>${s.apertura}</td>
                            <td>${s.cierre}</td>
                            <td class="text-center">
                              <button class="btn btn-sm btn-primary" data-dia="${en}" data-idx="${j}">
                                <i class="fas fa-pen"></i>
                              </button>
                            </td>
                            <td class="text-center">
                              <button class="btn btn-sm btn-danger" data-remove-dia="${en}" data-remove-idx="${j}">
                                <i class="fas fa-trash"></i>
                              </button>
                            </td>
                          </tr>
                        `);
                    }
                });
            }
        });

        // Inicializar tooltips en todos los iconos ℹ️ recién añadidos
        tbody.querySelectorAll('[data-bs-toggle="tooltip"]').forEach(el => {
            new bootstrap.Tooltip(el);
        });
    }

    /* ──────────── Abrir modal para editar o añadir ──────────── */
    tbody.addEventListener('click', e => {
        const btn = e.target.closest('button');
        if (!btn) return;

        if (btn.dataset.dia != null) {
            diaEdit = btn.dataset.dia;
            idxEdit = parseInt(btn.dataset.idx, 10);
            const slot = (horarios[diaEdit] || [])[idxEdit] || { apertura: '', cierre: '' };
            inputApertura.value = slot.apertura;
            inputCierre.value = slot.cierre;
            chkTodos.checked = false;
            modal.show();
        }
        if (btn.dataset.removeDia != null) {
            const d = btn.dataset.removeDia;
            const k = parseInt(btn.dataset.removeIdx, 10);
            if (horarios[d]) {
                horarios[d].splice(k, 1);
                renderTable();
            }
        }
    });

    /* ──────────── Guardar modal ──────────── */
    const form = document.getElementById('form-horario');
    
    form.addEventListener('submit', e => {
      e.preventDefault();
    
      // Limpia mensajes previos
      inputApertura.setCustomValidity('');
      inputCierre.setCustomValidity('');
    
      // Validación personalizada: cierre > apertura
      if (inputCierre.value <= inputApertura.value) {
        inputCierre.setCustomValidity('La hora de cierre debe ser posterior a la de apertura');
      }
    
      // Forzamos la comprobación de validez
      if (!form.checkValidity()) {
        form.classList.add('was-validated');        // Bootstrap mostrará los .invalid-feedback
        return;                                     // No seguimos con la lógica de guardado
      }
    
      // Si todo es válido, recogemos valores y guardamos
      const a = inputApertura.value, c = inputCierre.value;
      if (chkTodos.checked) {
        EN.forEach(d => horarios[d] = [{ apertura: a, cierre: c }]);
      } else {
        if (!horarios[diaEdit]) horarios[diaEdit] = [];
        if (idxEdit >= 0 && idxEdit < horarios[diaEdit].length) {
          horarios[diaEdit][idxEdit] = { apertura: a, cierre: c };
        } else {
          horarios[diaEdit].push({ apertura: a, cierre: c });
        }
      }
      modal.hide();
      renderTable();
    });
    

    /* ──────────── Persistir cambios ──────────── */
    btnGuardar.addEventListener('click', () => {
        const payload = [];
        EN.forEach(d => {
            (horarios[d] || []).forEach(s => {
                payload.push({ dia: d, apertura: s.apertura, cierre: s.cierre });
            });
        });
        fetch('/api/restaurants/schedule', {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'X-Requested-With': 'XMLHttpRequest'
            },
            body: JSON.stringify(payload)
        })
        .then(r => {
            if (r.ok) {
                Swal.fire({
                    toast: true, position: 'top-end', icon: 'success',
                    title: 'Horarios guardados', timer: 2000, showConfirmButton: false
                });
            } else {
                return r.text().then(t => Promise.reject(t || r.status));
            }
        })
        .catch(err => Swal.fire('Error', err, 'error'));
    });
});
