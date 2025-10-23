package com.umg.roboteducativo.controller;

import com.umg.roboteducativo.dto.ApiResponseDTO;
import com.umg.roboteducativo.dto.BitacoraDTO;
import com.umg.roboteducativo.service.BitacoraService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/bitacora")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BitacoraController {

    private final BitacoraService bitacoraService;

    /**
     * GET /api/bitacora
     * Obtiene toda la bitácora ordenada por fecha descendente
     */
    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<BitacoraDTO>>> obtenerTodaLaBitacora() {
        try {
            List<BitacoraDTO> bitacora = bitacoraService.obtenerTodaLaBitacora();
            return ResponseEntity.ok(
                ApiResponseDTO.success("Bitácora obtenida exitosamente", bitacora)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDTO.error("Error al obtener bitácora: " + e.getMessage()));
        }
    }

    /**
     * GET /api/bitacora/ultimas
     * Obtiene las últimas N entradas de la bitácora
     */
    @GetMapping("/ultimas")
    public ResponseEntity<ApiResponseDTO<List<BitacoraDTO>>> obtenerUltimas(
            @RequestParam(defaultValue = "50") int limite) {
        try {
            List<BitacoraDTO> bitacora = bitacoraService.obtenerUltimas(limite);
            return ResponseEntity.ok(
                ApiResponseDTO.success("Últimas entradas obtenidas", bitacora)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDTO.error("Error al obtener bitácora: " + e.getMessage()));
        }
    }

    /**
     * GET /api/bitacora/administrador/{administradorId}
     * Obtiene la bitácora de un administrador específico
     */
    @GetMapping("/administrador/{administradorId}")
    public ResponseEntity<ApiResponseDTO<List<BitacoraDTO>>> obtenerPorAdministrador(
            @PathVariable Integer administradorId) {
        try {
            List<BitacoraDTO> bitacora = bitacoraService.obtenerPorAdministrador(administradorId);
            return ResponseEntity.ok(
                ApiResponseDTO.success("Bitácora del administrador obtenida", bitacora)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDTO.error("Error al obtener bitácora: " + e.getMessage()));
        }
    }

    /**
     * GET /api/bitacora/accion/{accion}
     * Obtiene la bitácora filtrada por tipo de acción
     */
    @GetMapping("/accion/{accion}")
    public ResponseEntity<ApiResponseDTO<List<BitacoraDTO>>> obtenerPorAccion(
            @PathVariable String accion) {
        try {
            List<BitacoraDTO> bitacora = bitacoraService.obtenerPorAccion(accion);
            return ResponseEntity.ok(
                ApiResponseDTO.success("Bitácora filtrada por acción", bitacora)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDTO.error("Error al obtener bitácora: " + e.getMessage()));
        }
    }

    /**
     * GET /api/bitacora/rango
     * Obtiene la bitácora en un rango de fechas
     */
    @GetMapping("/rango")
    public ResponseEntity<ApiResponseDTO<List<BitacoraDTO>>> obtenerPorRangoFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        try {
            List<BitacoraDTO> bitacora = bitacoraService.obtenerPorRangoFechas(fechaInicio, fechaFin);
            return ResponseEntity.ok(
                ApiResponseDTO.success("Bitácora del rango obtenida", bitacora)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDTO.error("Error al obtener bitácora: " + e.getMessage()));
        }
    }

    /**
     * GET /api/bitacora/buscar
     * Busca en la bitácora por descripción
     */
    @GetMapping("/buscar")
    public ResponseEntity<ApiResponseDTO<List<BitacoraDTO>>> buscarPorDescripcion(
            @RequestParam String texto) {
        try {
            List<BitacoraDTO> bitacora = bitacoraService.buscarPorDescripcion(texto);
            return ResponseEntity.ok(
                ApiResponseDTO.success("Búsqueda completada", bitacora)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDTO.error("Error en la búsqueda: " + e.getMessage()));
        }
    }

    /**
     * GET /api/bitacora/estadisticas
     * Obtiene estadísticas de acciones
     */
    @GetMapping("/estadisticas")
    public ResponseEntity<ApiResponseDTO<List<Object[]>>> obtenerEstadisticasPorAccion() {
        try {
            List<Object[]> estadisticas = bitacoraService.obtenerEstadisticasPorAccion();
            return ResponseEntity.ok(
                ApiResponseDTO.success("Estadísticas de acciones obtenidas", estadisticas)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDTO.error("Error al obtener estadísticas: " + e.getMessage()));
        }
    }
}