document.addEventListener('DOMContentLoaded', async () => {
    const { data: horarios } = await fetch('/api/restaurant/schedule', {
        headers: { 'X-Requested-With': 'XMLHttpRequest' }})
        .then(r => r.json());      

    // ---------- FullCalendar ----------
    const calendar = new FullCalendar.Calendar(document.getElementById('calendar'), {
        initialView: 'timeGridWeek',
        slotMinTime: '06:00:00',
        slotMaxTime: '02:00:00',
        locale: 'es',
        editable: true,
        selectable: true,
        height: 'auto',
        events: horarios.map(h => ({
            id: crypto.randomUUID(),
            start: dayToDate(h.dia, h.apertura),
            end: dayToDate(h.dia, h.cierre),
            display: 'background'
        })),
        select: info => abrirModal(null, info),
        eventClick: info => abrirModal(info.event, null),
        eventDrop: syncConBackend,
        eventResize: syncConBackend
    });
    calendar.render();

    // ---------- Helpers ----------
    function dayToDate(dia, time) {
        //Monday
        const base = dayjs().startOf('week').add(1, 'day');
        return base.add(dia - 1, 'day').set(time.split(':'));
    }

    async function abrirModal(evento, info) {
        const { value: form } = await Swal.fire({
            title: evento ? 'Editar horario' : 'Nuevo horario',
            html: `
          <input id="dia" class="swal2-input" placeholder="LUNES" value="${evento ? evento.start.format('dddd').toUpperCase() : ''}">
          <input id="desde" type="time" class="swal2-input" value="${evento ? evento.start.format('HH:mm') : ''}">
          <input id="hasta" type="time" class="swal2-input" value="${evento ? evento.end.format('HH:mm') : ''}">
        `,
            focusConfirm: false,
            preConfirm: () => ({
                dia: document.getElementById('dia').value,
                desde: document.getElementById('desde').value,
                hasta: document.getElementById('hasta').value
            })
        });
        if (!form) return;

        // Crear o actualizar evento en calendario
        if (evento) {
            evento.setStart(dayToDate(form.dia, form.desde));
            evento.setEnd(dayToDate(form.dia, form.hasta));
        } else {
            calendar.addEvent({
                start: dayToDate(form.dia, form.desde),
                end: dayToDate(form.dia, form.hasta),
                id: crypto.randomUUID()
            });
        }
        syncConBackend();
    }

    async function syncConBackend() {
        const payload = calendar.getEvents().map(e => ({
            dia: DayOfWeek.of(e.start.getDay()).name(),
            apertura: dayjs(e.start).format('HH:mm'),
            cierre: dayjs(e.end).format('HH:mm')
        }));
        await fetch(`/api/restaurant/schedule`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json', 'X-Requested-With': 'XMLHttpRequest' },
            body: payload
        });
        Swal.fire({ toast: true, position: 'top-end', icon: 'success', title: 'Horarios guardados', timer: 2000, showConfirmButton: false });
    }
});  