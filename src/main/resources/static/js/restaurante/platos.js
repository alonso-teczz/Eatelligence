document.querySelectorAll('[data-bs-toggle="tooltip"]')
    .forEach(el => new bootstrap.Tooltip(el));

document.addEventListener("DOMContentLoaded", function () {
    document.querySelectorAll("form[data-cart-form]").forEach(form => {
        form.addEventListener("submit", function (event) {
            event.preventDefault();

            const formData = new FormData(form);
            const restauranteId = form.dataset.restauranteId;
            const platoId = formData.get("platoId");

            const button = form.querySelector("[data-submit-btn]");
            const spinner = button.querySelector("[data-spinner]");
            const content = button.querySelector("[data-btn-content]");

            const csrfToken = document.getElementById('csrfToken').value;
            const csrfHeader = document.getElementById('csrfHeader').value;

            button.disabled = true;
            spinner.classList.remove("d-none");
            content.classList.add("d-none");

            fetch(`/cart/${restauranteId}/add`, {
                method: "POST",
                headers: {
                    "X-Requested-With": "XMLHttpRequest",
                    [csrfHeader]: csrfToken
                },
                body: new URLSearchParams({
                    platoId: platoId,
                    cantidad: 1
                })
            })
            .then(response => {
                if (!response.ok) throw new Error("Error al añadir al carrito");
                return response.text();
            })
            .then(html => {
                const cartContainer = document.getElementById("cart-container");
                if (cartContainer) {
                    cartContainer.innerHTML = html;
                }
            })
            .catch(error => {
                console.error(error);
                alert("Hubo un error al añadir el plato al carrito.");
            })
            .finally(() => {
                button.disabled = false;
                spinner.classList.add("d-none");
                content.classList.remove("d-none");
            });
        });
    });
});    