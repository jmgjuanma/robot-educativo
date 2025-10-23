/**
 * Configuración y Variables Globales
 */
//const API_BASE_URL = 'http://localhost:8080/api';
//const API_BASE_URL = 'http://52.15.106.18:8080/api';
const API_BASE_URL = 'http://ec2-52-15-106-18.us-east-2.compute.amazonaws.com:8080/api';
const GRID_SIZE = 5;

let caminoActual = [];
let pistaActualId = null;

/**
 * Inicialización
 */
document.addEventListener('DOMContentLoaded', async () => {
    // Verificar autenticación primero
    const autenticado = await verificarAutenticacion();
    if (!autenticado) {
        return; // Se redirigirá al login
    }

    inicializarConfiguracion();
    configurarEventListeners();
    cargarPistasExistentes();
    actualizarInfoUsuario();
});

/**
 * Inicializa el tablero configurable
 */
function inicializarConfiguracion() {
    const tablero = document.getElementById('tablero');
    tablero.innerHTML = '';
    tablero.style.gridTemplateColumns = `repeat(${GRID_SIZE}, 1fr)`;
    tablero.style.gridTemplateRows = `repeat(${GRID_SIZE}, 1fr)`;

    // Crear cuadrícula configurable
    for (let y = 0; y < GRID_SIZE; y++) {
        for (let x = 0; x < GRID_SIZE; x++) {
            const celda = document.createElement('div');
            celda.className = 'celda configurable';
            celda.dataset.x = x;
            celda.dataset.y = y;

            // Evento click para marcar/desmarcar
            celda.addEventListener('click', () => toggleCelda(x, y));

            tablero.appendChild(celda);
        }
    }
}

/**
 * Configura los event listeners
 */
function configurarEventListeners() {
    document.getElementById('btnGuardar').addEventListener('click', guardarPista);
    document.getElementById('btnBorrar').addEventListener('click', borrarPista);
    document.getElementById('btnLimpiar').addEventListener('click', limpiarTablero);
    document.getElementById('btnVolver').addEventListener('click', () => {
        window.location.href = 'index.html';
    });
    document.getElementById('btnAdmin').addEventListener('click', () => {
        window.location.href = 'admin.html';
    });
}

/**
 * Alterna el estado de una celda (camino/vacío)
 */
function toggleCelda(x, y) {
    const celda = document.querySelector(`.celda[data-x="${x}"][data-y="${y}"]`);
    const index = caminoActual.findIndex(p => p.x === x && p.y === y);

    if (index >= 0) {
        // Ya está en el camino, quitarlo
        caminoActual.splice(index, 1);
        celda.classList.remove('camino');
    } else {
        // Agregar al camino
        caminoActual.push({ x, y });
        celda.classList.add('camino');
    }
}

/**
 * Limpia el tablero
 */
function limpiarTablero() {
    caminoActual = [];
    pistaActualId = null;
    document.querySelectorAll('.celda').forEach(celda => {
        celda.classList.remove('camino');
    });
}

/**
 * Guarda la pista en el backend
 */
async function guardarPista() {
    if (caminoActual.length < 2) {
        alert('❌ Debes marcar al menos 2 celdas para crear un camino');
        return;
    }

    const nombre = prompt(pistaActualId ? 
        'Confirma o edita el nombre de la pista:' : 
        'Nombre de la pista:'
    );
    
    if (!nombre || nombre.trim() === '') {
        alert('❌ Debes ingresar un nombre para la pista');
        return;
    }

    try {
        const configuracionJson = JSON.stringify(caminoActual);
        const username = getUsername();
        
        let url = `${API_BASE_URL}/pistas`;
        let method = 'POST';
        
        // Si estamos editando una pista existente
        if (pistaActualId) {
            url = `${API_BASE_URL}/pistas/${pistaActualId}?username=${username}`;
            method = 'PUT';
            console.log('Editando pista ID:', pistaActualId);
        } else {
            url = `${API_BASE_URL}/pistas?username=${username}`;
            console.log('Creando nueva pista');
        }

        const response = await fetchWithAuth(url, {
            method: method,
            body: JSON.stringify({
                nombre: nombre,
                configuracionJson: configuracionJson,
                activa: true
            })
        });

        const data = await response.json();

        if (data.success) {
            alert(pistaActualId ? 
                '✅ Pista actualizada exitosamente' : 
                '✅ Pista guardada exitosamente'
            );
            pistaActualId = data.data.id;
            cargarPistasExistentes();
        } else {
            alert('❌ Error: ' + data.message);
        }
    } catch (error) {
        console.error('Error al guardar pista:', error);
        alert('❌ Error al guardar la pista');
    }
}

/**
 * Borra la pista actual
 */
async function borrarPista() {
    if (!pistaActualId) {
        alert('No hay una pista seleccionada para borrar');
        return;
    }

    if (!confirm('¿Estás seguro de que deseas borrar esta pista?')) {
        return;
    }

    try {
        const username = getUsername();
        const response = await fetchWithAuth(`${API_BASE_URL}/pistas/${pistaActualId}?username=${username}`, {
            method: 'DELETE'
        });

        const data = await response.json();

        if (data.success) {
            alert('Pista borrada exitosamente');
            limpiarTablero();
            cargarPistasExistentes();
        } else {
            alert('Error: ' + data.message);
        }
    } catch (error) {
        console.error('Error al borrar pista:', error);
        alert('Error al borrar la pista');
    }
}

/**
 * Carga todas las pistas existentes
 */
async function cargarPistasExistentes() {
    try {
        const response = await fetchWithAuth(`${API_BASE_URL}/pistas/todas`);
        const data = await response.json();

        if (data.success) {
            renderizarListaPistas(data.data);
        }
    } catch (error) {
        console.error('Error al cargar pistas:', error);
    }
}

/**
 * Renderiza la lista de pistas existentes
 */
function renderizarListaPistas(pistas) {
    const lista = document.getElementById('listaPistas');
    
    if (pistas.length === 0) {
        lista.innerHTML = '<p>No hay pistas guardadas</p>';
        return;
    }

    lista.innerHTML = '';
    
    pistas.forEach(pista => {
        const item = document.createElement('div');
        item.className = 'pista-item';
        
        const estado = pista.activa ? '✅' : '❌';
        
        item.innerHTML = `
            <div>
                <strong>${estado} ${pista.nombre}</strong>
                <br>
                <small>Creada: ${new Date(pista.fechaCreacion).toLocaleDateString()}</small>
            </div>
            <button class="btn-cargar-pista" onclick="cargarPista(${pista.id})">
                📝 Editar
            </button>
        `;
        
        lista.appendChild(item);
    });
}

/**
 * Carga una pista específica para editarla
 */
async function cargarPista(id) {
    try {
        const response = await fetchWithAuth(`${API_BASE_URL}/pistas/${id}`);
        const data = await response.json();

        if (data.success) {
            const pista = data.data;
            
            // IMPORTANTE: Establecer el ID de la pista actual
            pistaActualId = pista.id;
            caminoActual = JSON.parse(pista.configuracionJson);
            
            // Limpiar tablero
            document.querySelectorAll('.celda').forEach(celda => {
                celda.classList.remove('camino');
            });
            
            // Marcar camino
            caminoActual.forEach(punto => {
                const celda = document.querySelector(`.celda[data-x="${punto.x}"][data-y="${punto.y}"]`);
                if (celda) {
                    celda.classList.add('camino');
                }
            });

            alert(`✅ Pista "${pista.nombre}" cargada para editar\n\nID: ${pistaActualId}`);
        }
    } catch (error) {
        console.error('Error al cargar pista:', error);
        alert('❌ Error al cargar la pista');
    }
}