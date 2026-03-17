const userTypeSelect = document.getElementById('userType');
const clienteFields = document.getElementById('clienteFields');
const proveedorFields = document.getElementById('proveedorFields');
const form = document.getElementById('registerForm');
const errorMsg = document.getElementById('errorMsg');

userTypeSelect.addEventListener('change', () => {
    const userType = userTypeSelect.value;

    if (userType === 'cliente') {
    clienteFields.classList.remove('hidden');
    proveedorFields.classList.add('hidden');
    } else if (userType === 'proveedor') {
    proveedorFields.classList.remove('hidden');
    clienteFields.classList.add('hidden');
    } else {
    clienteFields.classList.add('hidden');
    proveedorFields.classList.add('hidden');
    }
});

form.addEventListener('submit', function(e) {
    e.preventDefault();

    const password = document.getElementById('password').value;
    const confirmPassword = document.getElementById('confirmPassword').value;

    if (password !== confirmPassword) {
    errorMsg.textContent = 'Las contraseñas no coinciden.';
    return;
    }

  // Aquí puedes enviar los datos al servidor con fetch()
    errorMsg.textContent = '';
    alert('¡Registro exitoso!');
    form.reset();
    clienteFields.classList.add('hidden');
    proveedorFields.classList.add('hidden');
});
