document.addEventListener('DOMContentLoaded', () => {
    /* ──────────── Referencias DOM ──────────── */
    const tbody = document.querySelector('#tabla-horarios tbody');
    const btnGuardar = document.getElementById('btn-guardar');
    const modal = new bootstrap.Modal(document.getElementById('modalHorario'));
    const inputApertura = document.getElementById('input-apertura');
    const inputCierre = document.getElementById('input-cierre');
    const chkTodos = document.getElementById('chk-aplicar-todos');

    /* ──────────── Constantes útiles ─────────── */
    const EN = ['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY'];
    const ES = ['Lunes', 'Martes', 'Miércoles', 'Jueves', 'Viernes', 'Sábado', 'Domingo'];

    /* ──────────── Estado in-memory ──────────── */
    let horarios = {};      // { MONDAY: {apertura:'09:00', cierre:'18:00'}, … }
    let diaEdit = null;    // Día que se está editando en el modal

    /* ──────────── Carga inicial ──────────── */
    fetch('/api/restaurant/schedule', { headers: { 'X-Requested-With': 'XMLHttpRequest' } })
        .then(r => r.json())
        .then(({ data }) => {
            data.forEach(h => horarios[h.dia] = { apertura: h.apertura, cierre: h.cierre });
            renderTable();
        });

    /* ──────────── Render de la tabla ──────────── */
    function renderTable() {
        tbody.innerHTML = '';
        EN.forEach((en, i) => {
            const h = horarios[en] || {};
            tbody.insertAdjacentHTML('beforeend', `
          <tr>
            <td>${ES[i]}</td>
            <td>${h.apertura ?? '--'}</td>
            <td>${h.cierre ?? '--'}</td>
            <td>
              <button class="btn btn-sm btn-primary" data-dia="${en}">
                <i class="fas fa-pen"></i>
              </button>
            </td>
          </tr>
        `);
        });
    }

    /* ──────────── Abrir modal ──────────── */
    tbody.addEventListener('click', e => {
        if (!e.target.closest('button[data-dia]')) return;

        diaEdit = e.target.closest('button[data-dia]').dataset.dia;
        const h = horarios[diaEdit] || {};
        inputApertura.value = h.apertura ?? '';
        inputCierre.value = h.cierre ?? '';
        chkTodos.checked = false;

        modal.show();
    });

    /* ──────────── Guardar cambios del modal ──────────── */
    document.querySelector('#modalHorario form')
        .addEventListener('submit', e => {
            e.preventDefault();
            const apertura = inputApertura.value;
            const cierre = inputCierre.value;
            if (!apertura || !cierre) return;

            if (chkTodos.checked) {
                EN.forEach(en => horarios[en] = { apertura, cierre });
            } else {
                horarios[diaEdit] = { apertura, cierre };
            }

            modal.hide();
            renderTable();
        });

    /* ──────────── Persistir en backend ──────────── */
    btnGuardar.addEventListener('click', () => {
        const payload = EN.filter(d => horarios[d])
            .map(d => ({ dia: d, ...horarios[d] }));

        fetch('/api/restaurant/schedule', {
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
                    return r.text().then(msg => { throw new Error(msg || r.status); });
                }
            })
            .catch(err => Swal.fire('Error', err.message, 'error'));
    });
});