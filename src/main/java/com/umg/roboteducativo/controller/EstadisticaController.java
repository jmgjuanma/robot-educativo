package com.umg.roboteducativo.controller;

import com.umg.roboteducativo.dto.ApiResponseDTO;
import com.umg.roboteducativo.dto.EstadisticaDTO;
import com.umg.roboteducativo.dto.ResumenEstadisticasDTO;
import com.umg.roboteducativo.service.EstadisticaService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/estadisticas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EstadisticaController {

    private final EstadisticaService estadisticaService;

    /**
     * GET /api/estadisticas/resumen
     * Obtiene el resumen general de estadísticas
     */
    @GetMapping("/resumen")
    public ResponseEntity<ApiResponseDTO<ResumenEstadisticasDTO>> obtenerResumenGeneral() {
        try {
            ResumenEstadisticasDTO resumen = estadisticaService.obtenerResumenGeneral();
            return ResponseEntity.ok(
                ApiResponseDTO.success("Resumen obtenido exitosamente", resumen)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDTO.error("Error al obtener resumen: " + e.getMessage()));
        }
    }

    /**
     * GET /api/estadisticas/hoy
     * Obtiene las estadísticas de hoy
     */
    @GetMapping("/hoy")
    public ResponseEntity<ApiResponseDTO<List<EstadisticaDTO>>> obtenerEstadisticasHoy() {
        try {
            List<EstadisticaDTO> estadisticas = estadisticaService.obtenerEstadisticasHoy();
            return ResponseEntity.ok(
                ApiResponseDTO.success("Estadísticas de hoy obtenidas", estadisticas)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDTO.error("Error al obtener estadísticas: " + e.getMessage()));
        }
    }

    /**
     * GET /api/estadisticas/pista/{pistaId}
     * Obtiene las estadísticas de una pista específica
     */
    @GetMapping("/pista/{pistaId}")
    public ResponseEntity<ApiResponseDTO<List<EstadisticaDTO>>> obtenerEstadisticasPorPista(
            @PathVariable Integer pistaId) {
        try {
            List<EstadisticaDTO> estadisticas = estadisticaService.obtenerEstadisticasPorPista(pistaId);
            return ResponseEntity.ok(
                ApiResponseDTO.success("Estadísticas de la pista obtenidas", estadisticas)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDTO.error("Error al obtener estadísticas: " + e.getMessage()));
        }
    }

    /**
     * GET /api/estadisticas/rango
     * Obtiene estadísticas en un rango de fechas
     */
    @GetMapping("/rango")
    public ResponseEntity<ApiResponseDTO<List<EstadisticaDTO>>> obtenerEstadisticasPorRango(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        try {
            List<EstadisticaDTO> estadisticas = estadisticaService.obtenerEstadisticasPorRango(fechaInicio, fechaFin);
            return ResponseEntity.ok(
                ApiResponseDTO.success("Estadísticas del rango obtenidas", estadisticas)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDTO.error("Error al obtener estadísticas: " + e.getMessage()));
        }
    }

    /**
     * GET /api/estadisticas/por-pista
     * Obtiene el resumen de estadísticas agrupadas por pista
     */
    @GetMapping("/por-pista")
    public ResponseEntity<ApiResponseDTO<List<Object[]>>> obtenerResumenPorPista() {
        try {
            List<Object[]> resumen = estadisticaService.obtenerResumenPorPista();
            return ResponseEntity.ok(
                ApiResponseDTO.success("Resumen por pista obtenido", resumen)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDTO.error("Error al obtener resumen: " + e.getMessage()));
        }
    }

    /**
     * GET /api/estadisticas/mas-visitadas
     * Obtiene las pistas más visitadas
     */
    @GetMapping("/mas-visitadas")
    public ResponseEntity<ApiResponseDTO<List<Object[]>>> obtenerPistasMasVisitadas(
            @RequestParam(defaultValue = "10") int limite) {
        try {
            List<Object[]> pistas = estadisticaService.obtenerPistasMasVisitadas(limite);
            return ResponseEntity.ok(
                ApiResponseDTO.success("Top pistas más visitadas", pistas)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDTO.error("Error al obtener ranking: " + e.getMessage()));
        }
    }

    /**
     * GET /api/estadisticas/mejor-tasa-exito
     * Obtiene las pistas con mejor tasa de éxito
     */
    @GetMapping("/mejor-tasa-exito")
    public ResponseEntity<ApiResponseDTO<List<Object[]>>> obtenerPistasConMejorTasaExito(
            @RequestParam(defaultValue = "10") int limite) {
        try {
            List<Object[]> pistas = estadisticaService.obtenerPistasConMejorTasaExito(limite);
            return ResponseEntity.ok(
                ApiResponseDTO.success("Top pistas con mejor tasa de éxito", pistas)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDTO.error("Error al obtener ranking: " + e.getMessage()));
        }
    }
}