/**
 * Clase Robot - Maneja la lógica de movimiento del robot
 */
class Robot {
    constructor() {
        this.x = 0;
        this.y = 0;
        this.direccion = 0; // 0=arriba, 1=derecha, 2=abajo, 3=izquierda
        this.camino = [];
        this.posicionInicial = { x: 0, y: 0 };
    }

    /**
     * Establece el camino que debe seguir el robot
     */
    setCamino(camino) {
        this.camino = camino;
        if (camino.length > 0) {
            this.posicionInicial = { x: camino[0].x, y: camino[0].y };
            this.x = camino[0].x;
            this.y = camino[0].y;
            this.calcularDireccionInicial();
        }
    }

    /**
     * Calcula la dirección inicial del robot basada en el segundo punto del camino
     */
    calcularDireccionInicial() {
        if (this.camino.length < 2) {
            this.direccion = 0; // Arriba por defecto
            return;
        }

        const siguiente = this.camino[1];
        const dx = siguiente.x - this.x;
        const dy = siguiente.y - this.y;

        if (dx === 1) this.direccion = 1; // Derecha
        else if (dx === -1) this.direccion = 3; // Izquierda
        else if (dy === 1) this.direccion = 2; // Abajo
        else if (dy === -1) this.direccion = 0; // Arriba
    }

    /**
     * Reinicia el robot a su posición inicial
     */
    reiniciar() {
        this.x = this.posicionInicial.x;
        this.y = this.posicionInicial.y;
        this.calcularDireccionInicial();
    }

    /**
     * Mueve el robot un paso adelante según su dirección actual
     */
    moverAdelante() {
        const movimientos = [
            { dx: 0, dy: -1 },  // Arriba
            { dx: 1, dy: 0 },   // Derecha
            { dx: 0, dy: 1 },   // Abajo
            { dx: -1, dy: 0 }   // Izquierda
        ];

        const mov = movimientos[this.direccion];
        this.x += mov.dx;
        this.y += mov.dy;
    }

    /**
     * Gira el robot 90° a la izquierda
     */
    girarIzquierda() {
        this.direccion = (this.direccion + 3) % 4; // Equivalente a -1 módulo 4
    }

    /**
     * Gira el robot 90° a la derecha
     */
    girarDerecha() {
        this.direccion = (this.direccion + 1) % 4;
    }

    /**
     * Verifica si el robot está en el camino válido
     */
    estaEnCamino() {
        return this.camino.some(punto => punto.x === this.x && punto.y === this.y);
    }

    /**
     * Verifica si el robot llegó al final del camino
     */
    llegoAlFinal() {
        if (this.camino.length === 0) return false;
        const ultimoPunto = this.camino[this.camino.length - 1];
        return this.x === ultimoPunto.x && this.y === ultimoPunto.y;
    }

    /**
     * Obtiene la posición actual del robot
     */
    getPosicion() {
        return { x: this.x, y: this.y };
    }

    /**
     * Obtiene la dirección actual del robot en grados
     */
    getDireccionGrados() {
        return this.direccion * 90;
    }

    /**
     * Obtiene el emoji del robot según su dirección
     */
    getRobotEmoji() {
        const emojis = ['⬆️', '➡️', '⬇️', '⬅️'];
        return emojis[this.direccion];
    }
}

/**
 * Clase MovimientosManager - Maneja la lista de movimientos programados
 */
class MovimientosManager {
    constructor() {
        this.movimientos = [];
    }

    /**
     * Agrega un movimiento a la lista
     */
    agregar(tipo) {
        const id = Date.now() + Math.random();
        this.movimientos.push({ id, tipo });
        return id;
    }

    /**
     * Elimina un movimiento por ID
     */
    eliminar(id) {
        this.movimientos = this.movimientos.filter(m => m.id !== id);
    }

    /**
     * Limpia todos los movimientos
     */
    limpiar() {
        this.movimientos = [];
    }

    /**
     * Obtiene todos los movimientos
     */
    obtener() {
        return this.movimientos;
    }

    /**
     * Expande los bucles en la lista de movimientos
     * Un bucle repite 1 vez los movimientos dentro de él
     */
    expandirBucles() {
        const expandidos = [];
        let dentroDelBucle = false;
        let movimientosBucle = [];

        for (const mov of this.movimientos) {
            if (mov.tipo === 'bucle') {
                if (dentroDelBucle) {
                    // Cerrar bucle actual
                    expandidos.push(...movimientosBucle);
                    expandidos.push(...movimientosBucle); // Repetir 1 vez
                    movimientosBucle = [];
                    dentroDelBucle = false;
                } else {
                    // Abrir bucle
                    dentroDelBucle = true;
                }
            } else {
                if (dentroDelBucle) {
                    movimientosBucle.push(mov);
                } else {
                    expandidos.push(mov);
                }
            }
        }

        // Si quedó un bucle abierto, cerrarlo
        if (dentroDelBucle && movimientosBucle.length > 0) {
            expandidos.push(...movimientosBucle);
            expandidos.push(...movimientosBucle);
        }

        return expandidos;
    }
}

/**
 * Clase EjecutorMovimientos - Ejecuta los movimientos del robot
 */
class EjecutorMovimientos {
    constructor(robot, callback) {
        this.robot = robot;
        this.callback = callback;
        this.ejecutando = false;
    }

    /**
     * Ejecuta una lista de movimientos paso a paso
     */
    async ejecutar(movimientos) {
        if (this.ejecutando) return;
        
        this.ejecutando = true;
        const expandidos = this.expandirMovimientos(movimientos);

        for (let i = 0; i < expandidos.length; i++) {
            const mov = expandidos[i];
            
            // Ejecutar el movimiento
            this.ejecutarMovimiento(mov.tipo);
            
            // Notificar al callback
            if (this.callback) {
                const resultado = this.callback(i, mov.tipo);
                if (!resultado.exito) {
                    this.ejecutando = false;
                    return { exito: false, mensaje: resultado.mensaje };
                }
            }

            // Esperar antes del siguiente movimiento
            await this.esperar(500);
        }

        this.ejecutando = false;
        
        // Verificar si llegó al final
        if (this.robot.llegoAlFinal()) {
            return { exito: true, mensaje: '¡Misión completada!' };
        } else {
            return { exito: false, mensaje: 'No llegaste al final del camino' };
        }
    }

    /**
     * Ejecuta un movimiento individual
     */
    ejecutarMovimiento(tipo) {
        switch(tipo) {
            case 'adelante':
                this.robot.moverAdelante();
                break;
            case 'girar-izquierda':
                this.robot.girarIzquierda();
                break;
            case 'girar-derecha':
                this.robot.girarDerecha();
                break;
        }
    }

    /**
     * Expande los bucles en los movimientos
     */
    expandirMovimientos(movimientos) {
        const expandidos = [];
        let dentroDelBucle = false;
        let movimientosBucle = [];

        for (const mov of movimientos) {
            if (mov.tipo === 'bucle') {
                if (dentroDelBucle) {
                    expandidos.push(...movimientosBucle);
                    expandidos.push(...movimientosBucle);
                    movimientosBucle = [];
                    dentroDelBucle = false;
                } else {
                    dentroDelBucle = true;
                }
            } else {
                if (dentroDelBucle) {
                    movimientosBucle.push(mov);
                } else {
                    expandidos.push(mov);
                }
            }
        }

        if (dentroDelBucle && movimientosBucle.length > 0) {
            expandidos.push(...movimientosBucle);
            expandidos.push(...movimientosBucle);
        }

        return expandidos;
    }

    /**
     * Función auxiliar para esperar
     */
    esperar(ms) {
        return new Promise(resolve => setTimeout(resolve, ms));
    }
}