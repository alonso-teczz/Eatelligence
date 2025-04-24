document.addEventListener('DOMContentLoaded', () => {
    // Inicializa la tabla con Simple-DataTables
    const tabla = document.querySelector('#tablaPlatos');
    if (tabla) {
        new simpleDatatables.DataTable(tabla);
    }

    // Validación del modal de creación
    const form = document.querySelector('#modalNuevoPlato form');
    if (form) {
        form.addEventListener('submit', function (e) {
            const primerInvalido = form.querySelector(':invalid');
            if (primerInvalido) {
                e.preventDefault();
                e.stopPropagation();
                form.querySelectorAll('.is-invalid').forEach(el => el.classList.remove('is-invalid'));
                primerInvalido.classList.add('is-invalid');
                primerInvalido.focus();
                primerInvalido.scrollIntoView({ behavior: 'smooth', block: 'center' });
            } else {
                actualizarHiddenInput();
            }
        });

        // Limpiar formulario al cerrar el modal
        const modal = document.getElementById('modalNuevoPlato');
        modal.addEventListener('hidden.bs.modal', function () {
            form.classList.remove('was-validated');
            form.reset();
            form.querySelectorAll('.is-invalid, .is-valid').forEach(el => el.classList.remove('is-invalid', 'is-valid'));
            document.getElementById('alergenosSeleccionados').innerHTML = '';
        });
    }

    // Mostrar modal si hay error de backend
    const errorPresente = document.querySelector('.alert.alert-danger');
    if (errorPresente) {
        setTimeout(() => {
            const modal = new bootstrap.Modal(document.getElementById('modalNuevoPlato'));
            modal.show();
        }, 350);
    }

    // Función para inline editing (nombre, descripcion, precio)
    document.querySelectorAll('.editable').forEach(celda => {
        celda.addEventListener('dblclick', () => {
            const original = celda.textContent.trim();
            const field = celda.dataset.field;
            const platoId = celda.closest('tr').dataset.id;

            const input = document.createElement('input');
            input.type = field === 'precio' ? 'number' : 'text';
            input.value = original;
            input.className = 'form-control';
            input.style.minWidth = '120px';

            input.addEventListener('keydown', async (e) => {
                if (e.key === 'Enter') {
                    const nuevoValor = input.value;
                    try {
                        const response = await fetch(`/admin/plates/update-field/${platoId}`, {
                            method: 'POST',
                            headers: {
                                'Content-Type': 'application/json'
                            },
                            body: JSON.stringify({ campo: field, valor: nuevoValor })
                        });
                        if (response.ok) {
                            celda.textContent = nuevoValor;
                        } else {
                            alert('Error al actualizar el campo');
                        }
                    } catch (err) {
                        alert('Error de red al actualizar');
                    }
                }
                if (e.key === 'Escape') {
                    celda.textContent = original;
                }
            });

            celda.textContent = '';
            celda.appendChild(input);
            input.focus();
        });
    });

    // Abrir modal al hacer click en los alérgenos
    document.querySelectorAll('.editable-alergenos').forEach(celda => {
        celda.addEventListener('click', () => {
            const id = celda.dataset.id;
            const modal = new bootstrap.Modal(document.getElementById(`modalEditarAlergenos-${id}`));
            modal.show();
        });
    });

    // Inicializa SortableJS en todos los modales de edición de alérgenos
    document.querySelectorAll('[id^="seleccionados-"]').forEach(lista => {
        const id = lista.id.split('-')[1];
        const input = document.getElementById(`inputAlergenos-${id}`);
        new Sortable(lista, {
            group: 'alergenos-edit',
            animation: 150,
            onSort: () => actualizarInput(id),
            onAdd: () => actualizarInput(id),
            onRemove: () => actualizarInput(id)
        });
    });

    document.querySelectorAll('[id^="disponibles-"]').forEach(lista => {
        new Sortable(lista, {
            group: 'alergenos-edit',
            animation: 150
        });
    });

    function actualizarInput(platoId) {
        const ul = document.getElementById(`seleccionados-${platoId}`);
        const input = document.getElementById(`inputAlergenos-${platoId}`);
        const ids = Array.from(ul.children).map(li => li.dataset.id);
        input.value = ids.join(',');
    }

    function actualizarHiddenInput() {
        const seleccionados = document.getElementById('alergenosSeleccionados');
        const hiddenInput = document.getElementById('alergenosInput');
        const ids = Array.from(seleccionados.children).map(li => li.dataset.id);
        hiddenInput.value = ids.join(',');
    }

    // Inicializa drag & drop para el modal de creación
    const disponibles = document.getElementById('alergenosDisponibles');
    const seleccionados = document.getElementById('alergenosSeleccionados');
    const hiddenInput = document.getElementById('alergenosInput');

    if (disponibles && seleccionados && hiddenInput) {
        new Sortable(disponibles, {
            group: 'alergenos',
            animation: 150
        });
        new Sortable(seleccionados, {
            group: 'alergenos',
            animation: 150,
            onSort: actualizarHiddenInput,
            onAdd: actualizarHiddenInput,
            onRemove: actualizarHiddenInput
        });
    }
});