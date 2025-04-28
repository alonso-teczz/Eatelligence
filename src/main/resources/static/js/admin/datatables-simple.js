window.addEventListener('DOMContentLoaded', () => {
    document.querySelectorAll('table[id^="tabla"]')
        .forEach(tabla => new simpleDatatables.DataTable(tabla, {
            searchable    : true,
            // fixedHeight   : true,
            perPage       : 10,
            perPageSelect : [5, 10, 25, 50, 100],
            labels : {
                placeholder : 'Buscar…',
                perPage     : 'registros por página',
                noRows      : 'No hay datos disponibles',
                info        : 'Mostrando {start} a {end} de {rows} registros',
            }
        }));
});