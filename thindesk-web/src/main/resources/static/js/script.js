const toggleBtn = document.querySelector('.toggle-btn');
if (toggleBtn) {
    toggleBtn.addEventListener('click', () => {
        document.getElementById('sidebar').classList.toggle('active');
    });
}

function toggleSubmenu(event, element) {
    event.preventDefault();
    element.parentElement.classList.toggle('open');
}

document.querySelectorAll('[data-auto-submit]').forEach((campo) => {
    campo.addEventListener('change', () => campo.form.submit());
});

document.querySelectorAll('form[data-confirm]').forEach((form) => {
    form.addEventListener('submit', (event) => {
        if (!confirm(form.dataset.confirm)) {
            event.preventDefault();
        }
    });
});
