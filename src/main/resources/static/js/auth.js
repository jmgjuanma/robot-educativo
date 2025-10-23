/**
 * Interceptor global para agregar el token JWT a todas las peticiones fetch
 */
(function() {
    // Guardar el fetch original
    const originalFetch = window.fetch;

    // Sobrescribir fetch para agregar automáticamente el token
    window.fetch = function(...args) {
        let [url, config] = args;

        // Si no hay config, crearlo
        if (!config) {
            config = {};
        }

        // Si no hay headers, crearlos
        if (!config.headers) {
            config.headers = {};
        }

        // Agregar token si existe y no es la URL de login
        const token = localStorage.getItem('token');
        if (token && !url.includes('/auth/login') && !url.includes('/auth/registrar')) {
            config.headers['Authorization'] = `Bearer ${token}`;
        }

        // Llamar al fetch original
        return originalFetch(url, config);
    };
})();


/**
 * Utilidades de Autenticación
 * Este archivo debe ser incluido en todas las páginas protegidas
 */
//const API_BASE_URL = 'http://localhost:8080/api';
//const AUTH_API_URL = 'http://52.15.106.18:8080/api';
const AUTH_API_URL = 'http://ec2-52-15-106-18.us-east-2.compute.amazonaws.com:8080/api';

/**
 * Obtiene el token del localStorage
 */
function getToken() {
    return localStorage.getItem('token');
}

/**
 * Obtiene el username del localStorage
 */
function getUsername() {
    return localStorage.getItem('username');
}

/**
 * Obtiene el nombre completo del localStorage
 */
function getNombre() {
    return localStorage.getItem('nombre');
}

/**
 * Verifica si el usuario está autenticado
 */
function isAuthenticated() {
    return getToken() !== null;
}

/**
 * Cierra la sesión del usuario
 */
function logout() {
    localStorage.clear();
    window.location.href = 'login.html';
}

/**
 * Verifica si la página actual requiere autenticación
 * Si no está autenticado, redirige al login
 */
async function verificarAutenticacion() {
    const token = getToken();

    if (!token) {
        window.location.href = 'login.html';
        return false;
    }

    try {
        const response = await fetch(`${AUTH_API_URL}/auth/validar`, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        const data = await response.json();

        if (!data.success || data.data !== true) {
            // Token inválido
            logout();
            return false;
        }

        return true;
    } catch (error) {
        console.error('Error al verificar autenticación:', error);
        return false;
    }
}

/**
 * Obtiene los headers con autenticación para peticiones fetch
 */
function getAuthHeaders() {
    const token = getToken();
    return {
        'Content-Type': 'application/json',
        'Authorization': token ? `Bearer ${token}` : ''
    };
}

/**
 * Realiza una petición fetch autenticada
 */
async function fetchWithAuth(url, options = {}) {
    const token = getToken();
    
    if (!token) {
        logout();
        throw new Error('No hay token de autenticación');
    }

    const headers = {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`,
        ...options.headers
    };

    try {
        const response = await fetch(url, {
            ...options,
            headers
        });

        // Si es 401 o 403, el token expiró o es inválido
        if (response.status === 401 || response.status === 403) {
            logout();
            throw new Error('Sesión expirada');
        }

        return response;
    } catch (error) {
        if (error.message === 'Sesión expirada') {
            throw error;
        }
        console.error('Error en petición autenticada:', error);
        throw error;
    }
}

/**
 * Actualiza la UI con información del usuario
 */
function actualizarInfoUsuario() {
    const nombre = getNombre();
    const username = getUsername();

    // Buscar elementos con clase 'user-nombre' y 'user-username'
    document.querySelectorAll('.user-nombre').forEach(el => {
        el.textContent = nombre || username;
    });

    document.querySelectorAll('.user-username').forEach(el => {
        el.textContent = username;
    });
}

/**
 * Agrega botón de logout si existe un elemento con id 'logout-btn'
 */
document.addEventListener('DOMContentLoaded', () => {
    const logoutBtn = document.getElementById('logout-btn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', (e) => {
            e.preventDefault();
            if (confirm('¿Seguro que deseas cerrar sesión?')) {
                logout();
            }
        });
    }

    // Actualizar info del usuario si hay elementos
    actualizarInfoUsuario();
});