package com.umg.roboteducativo.service;

import com.umg.roboteducativo.dto.EstadisticaDTO;
import com.umg.roboteducativo.dto.ResumenEstadisticasDTO;
import com.umg.roboteducativo.model.Estadistica;
import com.umg.roboteducativo.model.Pista;
import com.umg.roboteducativo.repository.AdministradorRepository;
import com.umg.roboteducativo.repository.EstadisticaRepository;
import com.umg.roboteducativo.repository.PistaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EstadisticaService {

    private final EstadisticaRepository estadisticaRepository;
    private final PistaRepository pistaRepository;
    private final AdministradorRepository administradorRepository;

    /**
     * Registra una visita a una pista
     */
    @Transactional
    public void registrarVisita(Integer pistaId) {
        LocalDate hoy = LocalDate.now();
        Estadistica estadistica = estadisticaRepository
                .findByPistaIdAndFecha(pistaId, hoy)
                .orElseGet(() -> crearNuevaEstadistica(pistaId, hoy));

        estadistica.incrementarVisitas();
        estadisticaRepository.save(estadistica);
    }

    /**
     * Registra una completación exitosa
     */
    @Transactional
    public void registrarExito(Integer pistaId) {
        LocalDate hoy = LocalDate.now();
        Estadistica estadistica = estadisticaRepository
                .findByPistaIdAndFecha(pistaId, hoy)
                .orElseGet(() -> crearNuevaEstadistica(pistaId, hoy));

        estadistica.incrementarExitos();
        estadisticaRepository.save(estadistica);
    }

    /**
     * Registra una completación fallida
     */
    @Transactional
    public void registrarFallo(Integer pistaId) {
        LocalDate hoy = LocalDate.now();
        Estadistica estadistica = estadisticaRepository
                .findByPistaIdAndFecha(pistaId, hoy)
                .orElseGet(() -> crearNuevaEstadistica(pistaId, hoy));

        estadistica.incrementarFallos();
        estadisticaRepository.save(estadistica);
    }

    /**
     * Obtiene el resumen general de estadísticas
     */
    @Transactional(readOnly = true)
    public ResumenEstadisticasDTO obtenerResumenGeneral() {
        Long totalVisitas = estadisticaRepository.calcularTotalVisitas();
        Long totalExitos = estadisticaRepository.calcularTotalExitos();
        Long totalFallos = estadisticaRepository.calcularTotalFallos();
        Long totalPistasActivas = pistaRepository.countByActivaTrue();
        Long totalAdministradores = administradorRepository.countByActivoTrue();

        return new ResumenEstadisticasDTO(
            totalVisitas,
            totalExitos,
            totalFallos,
            totalPistasActivas,
            totalAdministradores
        );
    }

    /**
     * Obtiene estadísticas de una pista específica
     */
    @Transactional(readOnly = true)
    public List<EstadisticaDTO> obtenerEstadisticasPorPista(Integer pistaId) {
        return estadisticaRepository.findByPistaId(pistaId).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene estadísticas en un rango de fechas
     */
    @Transactional(readOnly = true)
    public List<EstadisticaDTO> obtenerEstadisticasPorRango(LocalDate fechaInicio, LocalDate fechaFin) {
        return estadisticaRepository.findByFechaBetween(fechaInicio, fechaFin).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene las estadísticas de hoy
     */
    @Transactional(readOnly = true)
    public List<EstadisticaDTO> obtenerEstadisticasHoy() {
        return estadisticaRepository.findEstadisticasHoy().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene el resumen de estadísticas por pista
     */
    @Transactional(readOnly = true)
    public List<Object[]> obtenerResumenPorPista() {
        return estadisticaRepository.obtenerResumenPorPista();
    }

    /**
     * Obtiene las pistas más visitadas
     */
    @Transactional(readOnly = true)
    public List<Object[]> obtenerPistasMasVisitadas(int limite) {
        return estadisticaRepository.obtenerPistasMasVisitadas(limite);
    }

    /**
     * Obtiene las pistas con mejor tasa de éxito
     */
    @Transactional(readOnly = true)
    public List<Object[]> obtenerPistasConMejorTasaExito(int limite) {
        return estadisticaRepository.obtenerPistasConMejorTasaExito(limite);
    }

    /**
     * Crea una nueva estadística para una pista y fecha
     */
    private Estadistica crearNuevaEstadistica(Integer pistaId, LocalDate fecha) {
        Pista pista = pistaRepository.findById(pistaId)
                .orElseThrow(() -> new RuntimeException("Pista no encontrada"));

        Estadistica estadistica = new Estadistica();
        estadistica.setPista(pista);
        estadistica.setFecha(fecha);
        estadistica.setTotalVisitas(0);
        estadistica.setCompletacionesExitosas(0);
        estadistica.setCompletacionesFallidas(0);

        return estadistica;
    }

    /**
     * Convierte una entidad Estadistica a EstadisticaDTO
     */
    private EstadisticaDTO convertirADTO(Estadistica estadistica) {
        return new EstadisticaDTO(
            estadistica.getId(),
            estadistica.getPista() != null ? estadistica.getPista().getNombre() : null,
            estadistica.getPista() != null ? estadistica.getPista().getId() : null,
            estadistica.getFecha(),
            estadistica.getTotalVisitas(),
            estadistica.getCompletacionesExitosas(),
            estadistica.getCompletacionesFallidas()
        );
    }
}