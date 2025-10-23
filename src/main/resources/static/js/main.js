/**
 * Configuración y Variables Globales
 */
const API_BASE_URL = 'http://localhost:8080/api';
const GRID_SIZE = 5;

let pistaActual = null;
let robot = new Robot();
let movimientosManager = new MovimientosManager();
let ejecutorMovimientos = null;

/**
 * Inicialización al cargar la página
 */
document.addEventListener('DOMContentLoaded', () => {
    inicializarJuego();
    configurarEventListeners();
    verificarSiEstaLogueado();
});

/**
 * Inicializa el juego cargando una pista aleatoria
 */
async function inicializarJuego() {
    try {
        await cargarPistaAleatoria();
    } catch (error) {
        console.error('Error al inicializar:', error);
        mostrarError('Error al cargar el juego. Por favor recarga la página.');
    }
}

/**
 * Configura todos los event listeners
 */
function configurarEventListeners() {
    // Botones de movimientos
    document.querySelectorAll('.btn-movimiento').forEach(btn => {
        btn.addEventListener('click', () => {
            const tipo = btn.dataset.movimiento;
            agregarMovimiento(tipo);
        });
    });

    // Botón ejecutar
    document.getElementById('btnEjecutar').addEventListener('click', ejecutarMovimientos);

    // Botón reiniciar
    document.getElementById('btnReiniciar').addEventListener('click', reiniciarJuego);

    // Botón configurar - verificar autenticación primero
    document.getElementById('btnConfigurar').addEventListener('click', () => {
        const token = localStorage.getItem('token');
        if (token) {
            window.location.href = 'configurar.html';
        } else {
            window.location.href = 'login.html';
        }
    });

    // Botón cerrar modal
    document.getElementById('btnCerrarModal').addEventListener('click', cerrarModal);
}

/**
 * Carga una pista aleatoria desde el backend
 */
async function cargarPistaAleatoria() {
    try {
        const response = await fetch(`${API_BASE_URL}/pistas/aleatoria`);
        const data = await response.json();

        if (data.success) {
            pistaActual = data.data;
            const camino = JSON.parse(pistaActual.configuracionJson);
            
            // Configurar robot
            robot.setCamino(camino);
            
            // Configurar ejecutor
            ejecutorMovimientos = new EjecutorMovimientos(robot, verificarMovimiento);
            
            // Renderizar
            document.getElementById('nombrePista').textContent = pistaActual.nombre;
            renderizarTablero(camino);
            
        } else {
            throw new Error(data.message);
        }
    } catch (error) {
        console.error('Error al cargar pista:', error);
        throw error;
    }
}

/**
 * Renderiza el tablero con la pista
 */
function renderizarTablero(camino) {
    const tablero = document.getElementById('tablero');
    tablero.innerHTML = '';
    tablero.style.gridTemplateColumns = `repeat(${GRID_SIZE}, 1fr)`;
    tablero.style.gridTemplateRows = `repeat(${GRID_SIZE}, 1fr)`;

    // Crear cuadrícula
    for (let y = 0; y < GRID_SIZE; y++) {
        for (let x = 0; x < GRID_SIZE; x++) {
            const celda = document.createElement('div');
            celda.className = 'celda';
            celda.dataset.x = x;
            celda.dataset.y = y;

            // Marcar camino
            if (camino.some(p => p.x === x && p.y === y)) {
                celda.classList.add('camino');
            }

            // Marcar posición inicial del robot
            if (x === robot.x && y === robot.y) {
                celda.classList.add('robot');
                celda.textContent = robot.getRobotEmoji();
            }

            tablero.appendChild(celda);
        }
    }
}

/**
 * Actualiza la visualización del robot en el tablero
 */
function actualizarRobotEnTablero() {
    // Limpiar todas las celdas del robot
    document.querySelectorAll('.celda').forEach(celda => {
        celda.classList.remove('robot');
        if (celda.textContent === '⬆️' || celda.textContent === '➡️' || 
            celda.textContent === '⬇️' || celda.textContent === '⬅️') {
            celda.textContent = '';
        }
    });

    // Marcar nueva posición
    const celda = document.querySelector(`.celda[data-x="${robot.x}"][data-y="${robot.y}"]`);
    if (celda) {
        celda.classList.add('robot');
        celda.textContent = robot.getRobotEmoji();
    }
}

/**
 * Agrega un movimiento a la lista
 */
function agregarMovimiento(tipo) {
    const id = movimientosManager.agregar(tipo);
    renderizarListaMovimientos();
}

/**
 * Renderiza la lista de movimientos programados
 */
function renderizarListaMovimientos() {
    const lista = document.getElementById('listMovimientos');
    const movimientos = movimientosManager.obtener();

    if (movimientos.length === 0) {
        lista.innerHTML = '<p class="empty-message">No hay movimientos programados</p>';
        return;
    }

    lista.innerHTML = '';
    
    movimientos.forEach((mov, index) => {
        const item = document.createElement('div');
        item.className = 'movimiento-item';
        if (mov.tipo === 'bucle') {
            item.classList.add('bucle');
        }

        const iconos = {
            'adelante': '⬆️',
            'girar-izquierda': '↰',
            'girar-derecha': '↱',
            'bucle': '🔁'
        };

        const nombres = {
            'adelante': 'Adelante',
            'girar-izquierda': 'Girar Izquierda',
            'girar-derecha': 'Girar Derecha',
            'bucle': 'Bucle'
        };

        item.innerHTML = `
            <span>${index + 1}. ${iconos[mov.tipo]} ${nombres[mov.tipo]}</span>
            <button class="btn-eliminar" onclick="eliminarMovimiento('${mov.id}')">❌</button>
        `;

        lista.appendChild(item);
    });
}

/**
 * Elimina un movimiento de la lista
 */
function eliminarMovimiento(id) {
    movimientosManager.eliminar(id);
    renderizarListaMovimientos();
}

/**
 * Ejecuta los movimientos programados
 */
async function ejecutarMovimientos() {
    const movimientos = movimientosManager.obtener();

    if (movimientos.length === 0) {
        mostrarError('No hay movimientos programados');
        return;
    }

    // Deshabilitar botones durante ejecución
    document.getElementById('btnEjecutar').disabled = true;
    document.getElementById('btnReiniciar').disabled = true;

    // Reiniciar robot
    robot.reiniciar();
    actualizarRobotEnTablero();

    // Ejecutar
    const resultado = await ejecutorMovimientos.ejecutar(movimientos);

    // Habilitar botones
    document.getElementById('btnEjecutar').disabled = false;
    document.getElementById('btnReiniciar').disabled = false;

    // Mostrar resultado
    if (resultado.exito) {
        await registrarExito();
        mostrarExito(resultado.mensaje);
    } else {
        await registrarFallo();
        mostrarError(resultado.mensaje);
    }
}

/**
 * Verifica si el movimiento es válido
 */
function verificarMovimiento(indice, tipo) {
    // Actualizar visualización
    actualizarRobotEnTablero();

    // Solo verificar si es un movimiento adelante
    if (tipo === 'adelante') {
        if (!robot.estaEnCamino()) {
            return { 
                exito: false, 
                mensaje: '¡El robot salió del camino! Inténtalo de nuevo.' 
            };
        }
    }

    return { exito: true };
}

/**
 * Reinicia el juego
 */
function reiniciarJuego() {
    movimientosManager.limpiar();
    renderizarListaMovimientos();
    robot.reiniciar();
    actualizarRobotEnTablero();
}

/**
 * Registra un éxito en el backend
 */
async function registrarExito() {
    try {
        await fetch(`${API_BASE_URL}/pistas/${pistaActual.id}/exito`, {
            method: 'POST'
        });
    } catch (error) {
        console.error('Error al registrar éxito:', error);
    }
}

/**
 * Registra un fallo en el backend
 */
async function registrarFallo() {
    try {
        await fetch(`${API_BASE_URL}/pistas/${pistaActual.id}/fallo`, {
            method: 'POST'
        });
    } catch (error) {
        console.error('Error al registrar fallo:', error);
    }
}

/**
 * Muestra modal de éxito
 */
function mostrarExito(mensaje) {
    const modal = document.getElementById('modalResultado');
    const titulo = document.getElementById('modalTitulo');
    const mensajeEl = document.getElementById('modalMensaje');
    const icono = document.getElementById('modalIcono');
    const content = modal.querySelector('.modal-content');

    content.classList.remove('error');
    titulo.textContent = '¡Misión Completada!';
    mensajeEl.textContent = mensaje;
    icono.textContent = '🎉';

    modal.classList.add('active');
}

/**
 * Muestra modal de error
 */
function mostrarError(mensaje) {
    const modal = document.getElementById('modalResultado');
    const titulo = document.getElementById('modalTitulo');
    const mensajeEl = document.getElementById('modalMensaje');
    const icono = document.getElementById('modalIcono');
    const content = modal.querySelector('.modal-content');

    content.classList.add('error');
    titulo.textContent = 'Inténtalo de nuevo';
    mensajeEl.textContent = mensaje;
    icono.textContent = '😕';

    modal.classList.add('active');
}

/**
 * Cierra el modal
 */
function cerrarModal() {
    const modal = document.getElementById('modalResultado');
    modal.classList.remove('active');
}

/**
 * Cierra modal al hacer clic fuera
 */
document.addEventListener('click', (e) => {
    const modal = document.getElementById('modalResultado');
    if (e.target === modal) {
        cerrarModal();
    }
});

/**
 * Verifica si el usuario está logueado para mostrar botón de admin
 */
function verificarSiEstaLogueado() {
    const token = localStorage.getItem('token');
    if (token) {
        const btnAdmin = document.getElementById('btnAdmin');
        if (btnAdmin) {
            btnAdmin.style.display = 'block';
            btnAdmin.addEventListener('click', () => {
                window.location.href = 'admin.html';
            });
        }
    }
}