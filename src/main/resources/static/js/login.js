/**
 * Configuración
 */
//const API_BASE_URL = 'http://localhost:8080/api';
//const API_BASE_URL = 'http://52.15.106.18:8080/api';
const API_BASE_URL = 'http://ec2-52-15-106-18.us-east-2.compute.amazonaws.com:8080/api';
/**
 * Inicialización
 */
document.addEventListener('DOMContentLoaded', () => {
    // Verificar si ya hay un token válido
    verificarSesion();

    // Configurar formulario
    const form = document.getElementById('loginForm');
    form.addEventListener('submit', handleLogin);

    // Limpiar alertas al escribir
    document.getElementById('username').addEventListener('input', limpiarAlerta);
    document.getElementById('password').addEventListener('input', limpiarAlerta);
});

/**
 * Verifica si hay una sesión activa
 */
async function verificarSesion() {
    const token = localStorage.getItem('token');
    
    if (token) {
        try {
            const response = await fetch(`${API_BASE_URL}/auth/validar`, {
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });

            const data = await response.json();

            if (data.success && data.data === true) {
                // Token válido, redirigir a configurar
                window.location.href = 'configurar.html';
            } else {
                // Token inválido, limpiar
                localStorage.clear();
            }
        } catch (error) {
            console.error('Error al verificar sesión:', error);
            localStorage.clear();
        }
    }
}

/**
 * Maneja el envío del formulario de login
 */
async function handleLogin(event) {
    event.preventDefault();

    const username = document.getElementById('username').value.trim();
    const password = document.getElementById('password').value;

    // Validaciones básicas
    if (!username || !password) {
        mostrarAlerta('Por favor completa todos los campos', 'error');
        return;
    }

    // Mostrar loading
    mostrarLoading(true);
    deshabilitarBoton(true);
    limpiarAlerta();

    try {
        const response = await fetch(`${API_BASE_URL}/auth/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                username: username,
                password: password
            })
        });

        const data = await response.json();

        if (data.success) {
            // Login exitoso
            const loginData = data.data;

            // Guardar en localStorage
            localStorage.setItem('token', loginData.token);
            localStorage.setItem('username', loginData.username);
            localStorage.setItem('nombre', loginData.nombre);
            localStorage.setItem('email', loginData.email);

            // Mostrar mensaje de éxito
            mostrarAlerta('¡Login exitoso! Redirigiendo...', 'success');

            // Redirigir después de 1 segundo
            setTimeout(() => {
                window.location.href = 'configurar.html';
            }, 1000);

        } else {
            // Login fallido
            mostrarAlerta(data.message || 'Credenciales inválidas', 'error');
            mostrarLoading(false);
            deshabilitarBoton(false);
        }

    } catch (error) {
        console.error('Error al iniciar sesión:', error);
        mostrarAlerta('Error de conexión. Verifica que el servidor esté activo.', 'error');
        mostrarLoading(false);
        deshabilitarBoton(false);
    }
}

/**
 * Muestra una alerta
 */
function mostrarAlerta(mensaje, tipo) {
    const alert = document.getElementById('alert');
    alert.textContent = mensaje;
    alert.className = `alert alert-${tipo} show`;
}

/**
 * Limpia la alerta
 */
function limpiarAlerta() {
    const alert = document.getElementById('alert');
    alert.className = 'alert';
}

/**
 * Muestra/oculta el loading
 */
function mostrarLoading(mostrar) {
    const loading = document.getElementById('loading');
    if (mostrar) {
        loading.classList.add('show');
    } else {
        loading.classList.remove('show');
    }
}

/**
 * Habilita/deshabilita el botón
 */
function deshabilitarBoton(deshabilitar) {
    const btn = document.getElementById('btnLogin');
    btn.disabled = deshabilitar;
}