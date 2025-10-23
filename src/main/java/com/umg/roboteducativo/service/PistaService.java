package com.umg.roboteducativo.service;

import com.umg.roboteducativo.dto.PistaDTO;
import com.umg.roboteducativo.model.Administrador;
import com.umg.roboteducativo.model.Pista;
import com.umg.roboteducativo.repository.AdministradorRepository;
import com.umg.roboteducativo.repository.PistaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PistaService {

    private final PistaRepository pistaRepository;
    private final AdministradorRepository administradorRepository;
    private final BitacoraService bitacoraService;

    /**
     * Obtiene una pista aleatoria activa
     * Método principal para cargar pistas en el juego
     */
    @Transactional(readOnly = true)
    public PistaDTO obtenerPistaAleatoria() {
        return pistaRepository.obtenerPistaAleatoria()
                .map(this::convertirADTO)
                .orElseThrow(() -> new RuntimeException("No hay pistas activas disponibles"));
    }

    /**
     * Obtiene todas las pistas activas
     */
    @Transactional(readOnly = true)
    public List<PistaDTO> obtenerPistasActivas() {
        return pistaRepository.findByActivaTrue().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene todas las pistas (activas e inactivas)
     */
    @Transactional(readOnly = true)
    public List<PistaDTO> obtenerTodasLasPistas() {
        return pistaRepository.findAll().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene una pista por ID
     */
    @Transactional(readOnly = true)
    public PistaDTO obtenerPorId(Integer id) {
        return pistaRepository.findById(id)
                .map(this::convertirADTO)
                .orElseThrow(() -> new RuntimeException("Pista no encontrada con ID: " + id));
    }

    /**
     * Crea una nueva pista
     */
    @Transactional
    public PistaDTO crearPista(PistaDTO pistaDTO, String username) {
        // Validar que no exista una pista con el mismo nombre
        if (pistaRepository.existsByNombre(pistaDTO.getNombre())) {
            throw new RuntimeException("Ya existe una pista con el nombre: " + pistaDTO.getNombre());
        }

        Pista pista = new Pista();
        pista.setNombre(pistaDTO.getNombre());
        pista.setConfiguracionJson(pistaDTO.getConfiguracionJson());
        pista.setActiva(true);

        // Buscar el administrador si se proporciona username
        if (username != null) {
            Administrador admin = administradorRepository.findByUsername(username)
                    .orElse(null);
            pista.setCreadoPor(admin);
        }

        Pista pistaGuardada = pistaRepository.save(pista);

        // Registrar en bitácora
        if (username != null) {
            bitacoraService.registrarAccion(
                username,
                "CREAR_PISTA",
                "Pista creada: " + pistaGuardada.getNombre(),
                null
            );
        }

        return convertirADTO(pistaGuardada);
    }

    /**
     * Actualiza una pista existente
     */
    @Transactional
    public PistaDTO actualizarPista(Integer id, PistaDTO pistaDTO, String username) {
        Pista pista = pistaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pista no encontrada con ID: " + id));

        // Validar nombre único si cambió
        if (!pista.getNombre().equals(pistaDTO.getNombre()) && 
            pistaRepository.existsByNombre(pistaDTO.getNombre())) {
            throw new RuntimeException("Ya existe una pista con el nombre: " + pistaDTO.getNombre());
        }

        pista.setNombre(pistaDTO.getNombre());
        pista.setConfiguracionJson(pistaDTO.getConfiguracionJson());
        if (pistaDTO.getActiva() != null) {
            pista.setActiva(pistaDTO.getActiva());
        }

        Pista pistaActualizada = pistaRepository.save(pista);

        // Registrar en bitácora
        if (username != null) {
            bitacoraService.registrarAccion(
                username,
                "ACTUALIZAR_PISTA",
                "Pista actualizada: " + pistaActualizada.getNombre(),
                null
            );
        }

        return convertirADTO(pistaActualizada);
    }

    /**
     * Elimina una pista (soft delete)
     */
    @Transactional
    public void eliminarPista(Integer id, String username) {
        Pista pista = pistaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pista no encontrada con ID: " + id));

        pista.setActiva(false);
        pistaRepository.save(pista);

        // Registrar en bitácora
        if (username != null) {
            bitacoraService.registrarAccion(
                username,
                "ELIMINAR_PISTA",
                "Pista eliminada (desactivada): " + pista.getNombre(),
                null
            );
        }
    }

    /**
     * Elimina permanentemente una pista
     */
    @Transactional
    public void eliminarPistaDefinitivamente(Integer id, String username) {
        Pista pista = pistaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pista no encontrada con ID: " + id));

        String nombrePista = pista.getNombre();
        pistaRepository.deleteById(id);

        // Registrar en bitácora
        if (username != null) {
            bitacoraService.registrarAccion(
                username,
                "ELIMINAR_PISTA_DEFINITIVO",
                "Pista eliminada permanentemente: " + nombrePista,
                null
            );
        }
    }

    /**
     * Busca pistas por nombre
     */
    @Transactional(readOnly = true)
    public List<PistaDTO> buscarPorNombre(String nombre) {
        return pistaRepository.buscarPorNombre(nombre).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /**
     * Cuenta las pistas activas
     */
    @Transactional(readOnly = true)
    public long contarPistasActivas() {
        return pistaRepository.countByActivaTrue();
    }

    /**
     * Convierte una entidad Pista a PistaDTO
     */
    private PistaDTO convertirADTO(Pista pista) {
        PistaDTO dto = new PistaDTO();
        dto.setId(pista.getId());
        dto.setNombre(pista.getNombre());
        dto.setConfiguracionJson(pista.getConfiguracionJson());
        dto.setFechaCreacion(pista.getFechaCreacion());
        dto.setFechaModificacion(pista.getFechaModificacion());
        dto.setActiva(pista.getActiva());
        
        if (pista.getCreadoPor() != null) {
            dto.setCreadoPor(pista.getCreadoPor().getNombre());
        }
        
        return dto;
    }
}