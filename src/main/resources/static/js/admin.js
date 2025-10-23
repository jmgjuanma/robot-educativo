/**
 * Configuración
 */
//const API_BASE_URL = 'http://localhost:8080/api';
//const API_BASE_URL = 'http://52.15.106.18:8080/api';
const API_BASE_URL = 'http://ec2-52-15-106-18.us-east-2.compute.amazonaws.com:8080/api';
/**
 * Inicialización
 */
document.addEventListener('DOMContentLoaded', async () => {
    // Verificar autenticación
    const autenticado = await verificarAutenticacion();
    if (!autenticado) {
        return;
    }

    // Actualizar info del usuario
    actualizarInfoUsuario();

    // Configurar navegación
    configurarNavegacion();

    // Cargar dashboard por defecto
    cargarDashboard();
});

/**
 * Configurar navegación entre secciones
 */
function configurarNavegacion() {
    document.querySelectorAll('.menu-link').forEach(link => {
        link.addEventListener('click', (e) => {
            e.preventDefault();
            
            const section = link.dataset.section;
            if (!section) return;

            // Actualizar menú activo
            document.querySelectorAll('.menu-link').forEach(l => l.classList.remove('active'));
            link.classList.add('active');

            // Mostrar sección correspondiente
            document.querySelectorAll('.section').forEach(s => s.classList.remove('active'));
            document.getElementById(`${section}-section`).classList.add('active');

            // Cargar datos de la sección
            switch(section) {
                case 'dashboard':
                    cargarDashboard();
                    break;
                case 'administradores':
                    cargarAdministradores();
                    break;
                case 'bitacora':
                    cargarBitacora();
                    break;
                case 'pistas':
                    cargarPistas();
                    break;
            }
        });
    });
}

/**
 * ========================================
 * DASHBOARD
 * ========================================
 */

/**
 * Carga el dashboard con estadísticas
 */
async function cargarDashboard() {
    try {
        // Cargar resumen general
        const response = await fetchWithAuth(`${API_BASE_URL}/estadisticas/resumen`);
        const data = await response.json();

        if (data.success) {
            const stats = data.data;
            document.getElementById('totalVisitas').textContent = stats.totalVisitas || 0;
            document.getElementById('totalExitos').textContent = stats.totalExitos || 0;
            document.getElementById('totalFallos').textContent = stats.totalFallos || 0;
            document.getElementById('porcentajeExito').textContent = 
                (stats.porcentajeExitoGlobal || 0).toFixed(1) + '%';
            document.getElementById('totalPistas').textContent = stats.totalPistasActivas || 0;
            document.getElementById('totalAdmins').textContent = stats.totalAdministradores || 0;
        }

        // Cargar estadísticas por pista
        const responsePistas = await fetchWithAuth(`${API_BASE_URL}/estadisticas/por-pista`);
        const dataPistas = await responsePistas.json();

        if (dataPistas.success) {
            renderizarEstadisticasPistas(dataPistas.data);
        }

    } catch (error) {
        console.error('Error al cargar dashboard:', error);
    }
}

/**
 * Renderiza tabla de estadísticas por pista
 */
function renderizarEstadisticasPistas(datos) {
    const tbody = document.querySelector('#tablaPistasStats tbody');
    
    if (!datos || datos.length === 0) {
        tbody.innerHTML = '<tr><td colspan="5">No hay datos disponibles</td></tr>';
        return;
    }

    tbody.innerHTML = '';
    
    datos.forEach(pista => {
        const [nombre, visitas, exitos, fallos] = pista;
        const total = exitos + fallos;
        const porcentaje = total > 0 ? ((exitos / total) * 100).toFixed(1) : 0;

        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${nombre}</td>
            <td>${visitas}</td>
            <td>${exitos}</td>
            <td>${fallos}</td>
            <td>${porcentaje}%</td>
        `;
        tbody.appendChild(tr);
    });
}

/**
 * ========================================
 * ADMINISTRADORES
 * ========================================
 */

/**
 * Carga la lista de administradores
 */
async function cargarAdministradores() {
    try {
        const response = await fetchWithAuth(`${API_BASE_URL}/administradores/todos`);
        const data = await response.json();

        if (data.success) {
            renderizarAdministradores(data.data);
        }
    } catch (error) {
        console.error('Error al cargar administradores:', error);
    }
}

/**
 * Renderiza tabla de administradores
 */
function renderizarAdministradores(admins) {
    const tbody = document.querySelector('#tablaAdministradores tbody');
    
    if (!admins || admins.length === 0) {
        tbody.innerHTML = '<tr><td colspan="6">No hay administradores</td></tr>';
        return;
    }

    tbody.innerHTML = '';
    
    admins.forEach(admin => {
        const estado = admin.activo ? '✅ Activo' : '❌ Inactivo';
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${admin.id}</td>
            <td>${admin.username}</td>
            <td>${admin.nombre}</td>
            <td>${admin.email}</td>
            <td>${estado}</td>
            <td>
                <button class="btn-action btn-edit" onclick="editarAdmin(${admin.id})">
                    ✏️ Editar
                </button>
                ${admin.username !== 'admin' ? `
                    <button class="btn-action btn-delete" onclick="eliminarAdmin(${admin.id})">
                        🗑️ Eliminar
                    </button>
                ` : ''}
            </td>
        `;
        tbody.appendChild(tr);
    });
}

/**
 * Muestra modal para nuevo administrador
 */
function mostrarModalNuevoAdmin() {
    document.getElementById('modalAdminTitulo').textContent = 'Nuevo Administrador';
    document.getElementById('adminId').value = '';
    document.getElementById('adminUsername').value = '';
    document.getElementById('adminNombre').value = '';
    document.getElementById('adminEmail').value = '';
    document.getElementById('adminPassword').value = '';
    document.getElementById('passwordGroup').style.display = 'block';
    document.getElementById('adminPassword').required = true;
    
    document.getElementById('modalAdmin').classList.add('active');
}

/**
 * Edita un administrador
 */
async function editarAdmin(id) {
    try {
        const response = await fetchWithAuth(`${API_BASE_URL}/administradores/${id}`);
        const data = await response.json();

        if (data.success) {
            const admin = data.data;
            
            document.getElementById('modalAdminTitulo').textContent = 'Editar Administrador';
            document.getElementById('adminId').value = admin.id;
            document.getElementById('adminUsername').value = admin.username;
            document.getElementById('adminNombre').value = admin.nombre;
            document.getElementById('adminEmail').value = admin.email;
            document.getElementById('passwordGroup').style.display = 'none';
            document.getElementById('adminPassword').required = false;
            
            document.getElementById('modalAdmin').classList.add('active');
        }
    } catch (error) {
        console.error('Error al cargar administrador:', error);
        alert('Error al cargar datos del administrador');
    }
}

/**
 * Elimina un administrador
 */
async function eliminarAdmin(id) {
    if (!confirm('¿Estás seguro de eliminar este administrador?')) {
        return;
    }

    try {
        const username = getUsername();
        const response = await fetchWithAuth(
            `${API_BASE_URL}/administradores/${id}?usernameModificador=${username}`, 
            { method: 'DELETE' }
        );
        const data = await response.json();

        if (data.success) {
            alert('Administrador eliminado exitosamente');
            cargarAdministradores();
        } else {
            alert('Error: ' + data.message);
        }
    } catch (error) {
        console.error('Error al eliminar administrador:', error);
        alert('Error al eliminar administrador');
    }
}

/**
 * Cierra modal de administrador
 */
function cerrarModalAdmin() {
    document.getElementById('modalAdmin').classList.remove('active');
}

/**
 * Maneja el submit del formulario de administrador
 */
document.getElementById('formAdmin').addEventListener('submit', async (e) => {
    e.preventDefault();

    const id = document.getElementById('adminId').value;
    const username = document.getElementById('adminUsername').value;
    const nombre = document.getElementById('adminNombre').value;
    const email = document.getElementById('adminEmail').value;
    const password = document.getElementById('adminPassword').value;

    try {
        const usernameActual = getUsername();
        let response;

        if (id) {
            // Actualizar
            response = await fetchWithAuth(
                `${API_BASE_URL}/administradores/${id}?usernameModificador=${usernameActual}`,
                {
                    method: 'PUT',
                    body: JSON.stringify({ id, username, nombre, email, activo: true })
                }
            );
        } else {
            // Crear - usar el endpoint de auth/registrar
            response = await fetchWithAuth(
                `${API_BASE_URL}/auth/registrar`,
                {
                    method: 'POST',
                    body: JSON.stringify({ username, nombre, email, password })
                }
            );
        }

        const data = await response.json();

        if (data.success) {
            alert(id ? 'Administrador actualizado' : 'Administrador creado exitosamente');
            cerrarModalAdmin();
            cargarAdministradores();
        } else {
            alert('Error: ' + data.message);
        }
    } catch (error) {
        console.error('Error al guardar administrador:', error);
        alert('Error al guardar administrador');
    }
});

/**
 * ========================================
 * BITÁCORA
 * ========================================
 */

/**
 * Carga la bitácora
 */
async function cargarBitacora() {
    try {
        const response = await fetchWithAuth(`${API_BASE_URL}/bitacora/ultimas?limite=100`);
        const data = await response.json();

        if (data.success) {
            renderizarBitacora(data.data);
        }
    } catch (error) {
        console.error('Error al cargar bitácora:', error);
    }
}

/**
 * Renderiza tabla de bitácora
 */
function renderizarBitacora(logs) {
    const tbody = document.querySelector('#tablaBitacora tbody');
    
    if (!logs || logs.length === 0) {
        tbody.innerHTML = '<tr><td colspan="5">No hay registros</td></tr>';
        return;
    }

    tbody.innerHTML = '';
    
    logs.forEach(log => {
        const fecha = new Date(log.fechaHora).toLocaleString('es-GT');
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${fecha}</td>
            <td>${log.username || 'Sistema'}</td>
            <td>${log.accion}</td>
            <td>${log.descripcion || '-'}</td>
            <td>${log.ipAddress || '-'}</td>
        `;
        tbody.appendChild(tr);
    });
}

/**
 * ========================================
 * PISTAS
 * ========================================
 */

/**
 * Carga la lista de pistas
 */
async function cargarPistas() {
    try {
        const response = await fetchWithAuth(`${API_BASE_URL}/pistas/todas`);
        const data = await response.json();

        if (data.success) {
            renderizarPistas(data.data);
        }
    } catch (error) {
        console.error('Error al cargar pistas:', error);
    }
}

/**
 * Renderiza tabla de pistas
 */
function renderizarPistas(pistas) {
    const tbody = document.querySelector('#tablaPistas tbody');
    
    if (!pistas || pistas.length === 0) {
        tbody.innerHTML = '<tr><td colspan="6">No hay pistas</td></tr>';
        return;
    }

    tbody.innerHTML = '';
    
    pistas.forEach(pista => {
        const fecha = new Date(pista.fechaCreacion).toLocaleDateString('es-GT');
        const estado = pista.activa ? '✅ Activa' : '❌ Inactiva';
        
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${pista.id}</td>
            <td>${pista.nombre}</td>
            <td>${pista.creadoPor || '-'}</td>
            <td>${fecha}</td>
            <td>${estado}</td>
            <td>
                <button class="btn-action btn-edit" onclick="exportarPista(${pista.id})">
                    📤 Exportar
                </button>
            </td>
        `;
        tbody.appendChild(tr);
    });
}

/**
 * Exporta una pista individual
 */
async function exportarPista(id) {
    try {
        const response = await fetchWithAuth(`${API_BASE_URL}/pistas/${id}`);
        const data = await response.json();

        if (data.success) {
            const pista = data.data;
            const blob = new Blob([JSON.stringify(pista, null, 2)], { type: 'application/json' });
            const url = URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = `pista-${pista.nombre}.json`;
            a.click();
            URL.revokeObjectURL(url);
        }
    } catch (error) {
        console.error('Error al exportar pista:', error);
        alert('Error al exportar pista');
    }
}

/**
 * Exporta todas las pistas
 */
async function exportarTodasLasPistas() {
    try {
        const response = await fetchWithAuth(`${API_BASE_URL}/pistas/todas`);
        const data = await response.json();

        if (data.success) {
            const blob = new Blob([JSON.stringify(data.data, null, 2)], { type: 'application/json' });
            const url = URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = `pistas-backup-${new Date().toISOString().split('T')[0]}.json`;
            a.click();
            URL.revokeObjectURL(url);
            
            alert('Pistas exportadas exitosamente');
        }
    } catch (error) {
        console.error('Error al exportar pistas:', error);
        alert('Error al exportar pistas');
    }
}

/**
 * Muestra modal para importar pistas
 */
function mostrarModalImportar() {
    document.getElementById('modalImportar').classList.add('active');
}

/**
 * Cierra modal de importar
 */
function cerrarModalImportar() {
    document.getElementById('modalImportar').classList.remove('active');
    document.getElementById('archivoImportar').value = '';
}

/**
 * Maneja el submit del formulario de importar
 */
document.getElementById('formImportar').addEventListener('submit', async (e) => {
    e.preventDefault();

    const archivo = document.getElementById('archivoImportar').files[0];
    if (!archivo) {
        alert('Selecciona un archivo');
        return;
    }

    try {
        const texto = await archivo.text();
        const pistas = JSON.parse(texto);
        const username = getUsername();

        // Verificar si es un array o un objeto individual
        const pistasArray = Array.isArray(pistas) ? pistas : [pistas];

        let exitosos = 0;
        let fallidos = 0;

        for (const pista of pistasArray) {
            try {
                const response = await fetchWithAuth(
                    `${API_BASE_URL}/pistas?username=${username}`,
                    {
                        method: 'POST',
                        body: JSON.stringify({
                            nombre: pista.nombre + ' (importada)',
                            configuracionJson: pista.configuracionJson,
                            activa: true
                        })
                    }
                );

                const data = await response.json();
                if (data.success) {
                    exitosos++;
                } else {
                    fallidos++;
                }
            } catch (error) {
                fallidos++;
            }
        }

        alert(`Importación completada:\n✅ Exitosas: ${exitosos}\n❌ Fallidas: ${fallidos}`);
        cerrarModalImportar();
        cargarPistas();

    } catch (error) {
        console.error('Error al importar:', error);
        alert('Error al leer el archivo. Verifica que sea un JSON válido.');
    }
});

/**
 * Cerrar modales al hacer clic fuera
 */
document.addEventListener('click', (e) => {
    if (e.target.classList.contains('modal')) {
        e.target.classList.remove('active');
    }
});