document.addEventListener("DOMContentLoaded", () => {
    const tabla = document.getElementById("tablaPlatos");

    tabla.addEventListener("dblclick", (e) => {
        const celda = e.target.closest(".editable");
        if (!celda) return;

        const valorOriginal = celda.textContent;
        const input = document.createElement("input");
        input.type = celda.dataset.field === "precio" ? "number" : "text";
        input.value = valorOriginal;
        input.classList.add("form-control", "form-control-sm");
        input.style.width = "100%";

        celda.textContent = "";
        celda.appendChild(input);
        input.focus();

        input.addEventListener("blur", async () => {
            const nuevoValor = input.value;
            const fila = celda.closest("tr");
            const platoId = fila.dataset.id;
            const campo = celda.dataset.field;

            if (nuevoValor !== valorOriginal) {
                const body = {};
                body[campo] = campo === "precio" ? parseFloat(nuevoValor) : nuevoValor;

                try {
                    const response = await fetch(`/api/plates/${platoId}`, {
                        method: "PUT",
                        headers: {
                            "Content-Type": "application/json"
                        },
                        body: JSON.stringify(body)
                    });

                    if (!response.ok) {
                        throw new Error("Error al actualizar");
                    }

                    celda.textContent = nuevoValor;
                } catch (err) {
                    console.error(err);
                    celda.textContent = valorOriginal;
                    alert("Error al actualizar el campo.");
                }
            } else {
                celda.textContent = valorOriginal;
            }
        });

        input.addEventListener("keydown", (event) => {
            if (event.key === "Enter") {
                input.blur();
            } else if (event.key === "Escape") {
                celda.textContent = valorOriginal;
            }
        });
    });
});
