package com.umg.roboteducativo.service;

import com.umg.roboteducativo.dto.BitacoraDTO;
import com.umg.roboteducativo.model.Administrador;
import com.umg.roboteducativo.model.Bitacora;
import com.umg.roboteducativo.repository.AdministradorRepository;
import com.umg.roboteducativo.repository.BitacoraRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BitacoraService {

    private final BitacoraRepository bitacoraRepository;
    private final AdministradorRepository administradorRepository;

    /**
     * Registra una acción en la bitácora
     */
    @Transactional
    public void registrarAccion(String username, String accion, String descripcion, String ipAddress) {
        Administrador admin = administradorRepository.findByUsername(username).orElse(null);

        Bitacora bitacora = new Bitacora();
        bitacora.setAdministrador(admin);
        bitacora.setAccion(accion);
        bitacora.setDescripcion(descripcion);
        bitacora.setIpAddress(ipAddress);

        bitacoraRepository.save(bitacora);
    }

    /**
     * Obtiene toda la bitácora ordenada por fecha descendente
     */
    @Transactional(readOnly = true)
    public List<BitacoraDTO> obtenerTodaLaBitacora() {
        return bitacoraRepository.findAllByOrderByFechaHoraDesc().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene las últimas N entradas de la bitácora
     */
    @Transactional(readOnly = true)
    public List<BitacoraDTO> obtenerUltimas(int limite) {
        return bitacoraRepository.findUltimas(limite).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene la bitácora de un administrador específico
     */
    @Transactional(readOnly = true)
    public List<BitacoraDTO> obtenerPorAdministrador(Integer administradorId) {
        return bitacoraRepository.findByAdministradorId(administradorId).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene la bitácora por tipo de acción
     */
    @Transactional(readOnly = true)
    public List<BitacoraDTO> obtenerPorAccion(String accion) {
        return bitacoraRepository.findByAccionOrderByFechaHoraDesc(accion).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene la bitácora en un rango de fechas
     */
    @Transactional(readOnly = true)
    public List<BitacoraDTO> obtenerPorRangoFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return bitacoraRepository.findByFechaHoraBetween(fechaInicio, fechaFin).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca en la bitácora por descripción
     */
    @Transactional(readOnly = true)
    public List<BitacoraDTO> buscarPorDescripcion(String texto) {
        return bitacoraRepository.buscarPorDescripcion(texto).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene estadísticas de acciones
     */
    @Transactional(readOnly = true)
    public List<Object[]> obtenerEstadisticasPorAccion() {
        return bitacoraRepository.obtenerEstadisticasPorAccion();
    }

    /**
     * Convierte una entidad Bitacora a BitacoraDTO
     */
    private BitacoraDTO convertirADTO(Bitacora bitacora) {
        BitacoraDTO dto = new BitacoraDTO();
        dto.setId(bitacora.getId());
        dto.setAccion(bitacora.getAccion());
        dto.setDescripcion(bitacora.getDescripcion());
        dto.setFechaHora(bitacora.getFechaHora());
        dto.setIpAddress(bitacora.getIpAddress());
        
        if (bitacora.getAdministrador() != null) {
            dto.setAdministrador(bitacora.getAdministrador().getNombre());
            dto.setUsername(bitacora.getAdministrador().getUsername());
        }
        
        return dto;
    }
}