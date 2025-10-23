package com.umg.roboteducativo.service;

import com.umg.roboteducativo.dto.AdministradorDTO;
import com.umg.roboteducativo.dto.CrearAdministradorDTO;
import com.umg.roboteducativo.model.Administrador;
import com.umg.roboteducativo.repository.AdministradorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service para la lógica de negocio de Administradores
 */
@Service
@RequiredArgsConstructor
public class AdministradorService {

    private final AdministradorRepository administradorRepository;
    private final BitacoraService bitacoraService;
    
    // Importar al inicio del archivo:
    // import org.springframework.security.crypto.password.PasswordEncoder;

    /**
     * Obtiene todos los administradores activos
     */
    @Transactional(readOnly = true)
    public List<AdministradorDTO> obtenerAdministradoresActivos() {
        return administradorRepository.findByActivoTrue().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene todos los administradores
     */
    @Transactional(readOnly = true)
    public List<AdministradorDTO> obtenerTodosLosAdministradores() {
        return administradorRepository.findAll().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene un administrador por ID
     */
    @Transactional(readOnly = true)
    public AdministradorDTO obtenerPorId(Integer id) {
        return administradorRepository.findById(id)
                .map(this::convertirADTO)
                .orElseThrow(() -> new RuntimeException("Administrador no encontrado con ID: " + id));
    }

    /**
     * Obtiene un administrador por username
     */
    @Transactional(readOnly = true)
    public AdministradorDTO obtenerPorUsername(String username) {
        return administradorRepository.findByUsername(username)
                .map(this::convertirADTO)
                .orElseThrow(() -> new RuntimeException("Administrador no encontrado con username: " + username));
    }

    /**
     * Crea un nuevo administrador
     */
    @Transactional
    public AdministradorDTO crearAdministrador(CrearAdministradorDTO crearDTO, String usernameCreador) {
        // Validar que no exista el username
        if (administradorRepository.existsByUsername(crearDTO.getUsername())) {
            throw new RuntimeException("Ya existe un administrador con el username: " + crearDTO.getUsername());
        }

        // Validar que no exista el email
        if (administradorRepository.existsByEmail(crearDTO.getEmail())) {
            throw new RuntimeException("Ya existe un administrador con el email: " + crearDTO.getEmail());
        }

        Administrador administrador = new Administrador();
        administrador.setUsername(crearDTO.getUsername());
        administrador.setPassword(crearDTO.getPassword());
        administrador.setNombre(crearDTO.getNombre());
        administrador.setEmail(crearDTO.getEmail());
        administrador.setActivo(true);

        Administrador adminGuardado = administradorRepository.save(administrador);

        // Registrar en bitácora
        if (usernameCreador != null) {
            bitacoraService.registrarAccion(
                usernameCreador,
                "CREAR_ADMINISTRADOR",
                "Administrador creado: " + adminGuardado.getUsername(),
                null
            );
        }

        return convertirADTO(adminGuardado);
    }

    /**
     * Actualiza un administrador existente
     */
    @Transactional
    public AdministradorDTO actualizarAdministrador(Integer id, AdministradorDTO adminDTO, String usernameModificador) {
        Administrador administrador = administradorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Administrador no encontrado con ID: " + id));

        // Validar username único si cambió
        if (!administrador.getUsername().equals(adminDTO.getUsername()) && 
            administradorRepository.existsByUsername(adminDTO.getUsername())) {
            throw new RuntimeException("Ya existe un administrador con el username: " + adminDTO.getUsername());
        }

        // Validar email único si cambió
        if (!administrador.getEmail().equals(adminDTO.getEmail()) && 
            administradorRepository.existsByEmail(adminDTO.getEmail())) {
            throw new RuntimeException("Ya existe un administrador con el email: " + adminDTO.getEmail());
        }

        administrador.setUsername(adminDTO.getUsername());
        administrador.setNombre(adminDTO.getNombre());
        administrador.setEmail(adminDTO.getEmail());
        if (adminDTO.getActivo() != null) {
            administrador.setActivo(adminDTO.getActivo());
        }

        Administrador adminActualizado = administradorRepository.save(administrador);

        // Registrar en bitácora
        if (usernameModificador != null) {
            bitacoraService.registrarAccion(
                usernameModificador,
                "ACTUALIZAR_ADMINISTRADOR",
                "Administrador actualizado: " + adminActualizado.getUsername(),
                null
            );
        }

        return convertirADTO(adminActualizado);
    }

    /**
     * Cambia la contraseña de un administrador
     */
    @Transactional
    public void cambiarPassword(Integer id, String nuevaPassword, String usernameModificador) {
        Administrador administrador = administradorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Administrador no encontrado con ID: " + id));
        administrador.setPassword(nuevaPassword);
        administradorRepository.save(administrador);

        // Registrar en bitácora
        if (usernameModificador != null) {
            bitacoraService.registrarAccion(
                usernameModificador,
                "CAMBIAR_PASSWORD",
                "Contraseña cambiada para: " + administrador.getUsername(),
                null
            );
        }
    }

    /**
     * Elimina un administrador (soft delete)
     */
    @Transactional
    public void eliminarAdministrador(Integer id, String usernameModificador) {
        Administrador administrador = administradorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Administrador no encontrado con ID: " + id));

        administrador.setActivo(false);
        administradorRepository.save(administrador);

        // Registrar en bitácora
        if (usernameModificador != null) {
            bitacoraService.registrarAccion(
                usernameModificador,
                "ELIMINAR_ADMINISTRADOR",
                "Administrador desactivado: " + administrador.getUsername(),
                null
            );
        }
    }

    /**
     * Busca administradores por nombre
     */
    @Transactional(readOnly = true)
    public List<AdministradorDTO> buscarPorNombre(String nombre) {
        return administradorRepository.buscarPorNombre(nombre).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /**
     * Convierte una entidad Administrador a AdministradorDTO
     */
    private AdministradorDTO convertirADTO(Administrador admin) {
        AdministradorDTO dto = new AdministradorDTO();
        dto.setId(admin.getId());
        dto.setUsername(admin.getUsername());
        dto.setNombre(admin.getNombre());
        dto.setEmail(admin.getEmail());
        dto.setFechaCreacion(admin.getFechaCreacion());
        dto.setActivo(admin.getActivo());
        return dto;
    }
}