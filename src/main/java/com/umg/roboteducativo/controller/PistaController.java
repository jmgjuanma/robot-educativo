package com.umg.roboteducativo.controller;

import com.umg.roboteducativo.dto.ApiResponseDTO;
import com.umg.roboteducativo.dto.PistaDTO;
import com.umg.roboteducativo.service.EstadisticaService;
import com.umg.roboteducativo.service.PistaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pistas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PistaController {

    private final PistaService pistaService;
    private final EstadisticaService estadisticaService;

    /**
     * GET /api/pistas/aleatoria
     * Obtiene una pista aleatoria para el juego
     * Este es el endpoint principal que usa el frontend
     */
    @GetMapping("/aleatoria")
    public ResponseEntity<ApiResponseDTO<PistaDTO>> obtenerPistaAleatoria() {
        try {
            PistaDTO pista = pistaService.obtenerPistaAleatoria();
            
            // Registrar visita
            estadisticaService.registrarVisita(pista.getId());
            
            return ResponseEntity.ok(
                ApiResponseDTO.success("Pista cargada exitosamente", pista)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDTO.error("Error al cargar pista: " + e.getMessage()));
        }
    }

    /**
     * GET /api/pistas
     * Obtiene todas las pistas activas
     */
    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<PistaDTO>>> obtenerPistasActivas() {
        try {
            List<PistaDTO> pistas = pistaService.obtenerPistasActivas();
            return ResponseEntity.ok(
                ApiResponseDTO.success("Pistas obtenidas exitosamente", pistas)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDTO.error("Error al obtener pistas: " + e.getMessage()));
        }
    }

    /**
     * GET /api/pistas/todas
     * Obtiene todas las pistas (activas e inactivas)
     */
    @GetMapping("/todas")
    public ResponseEntity<ApiResponseDTO<List<PistaDTO>>> obtenerTodasLasPistas() {
        try {
            List<PistaDTO> pistas = pistaService.obtenerTodasLasPistas();
            return ResponseEntity.ok(
                ApiResponseDTO.success("Pistas obtenidas exitosamente", pistas)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDTO.error("Error al obtener pistas: " + e.getMessage()));
        }
    }

    /**
     * GET /api/pistas/{id}
     * Obtiene una pista específica por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<PistaDTO>> obtenerPistaPorId(@PathVariable Integer id) {
        try {
            PistaDTO pista = pistaService.obtenerPorId(id);
            return ResponseEntity.ok(
                ApiResponseDTO.success("Pista encontrada", pista)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponseDTO.error(e.getMessage()));
        }
    }

    /**
     * POST /api/pistas
     * Crea una nueva pista
     */
    @PostMapping
    public ResponseEntity<ApiResponseDTO<PistaDTO>> crearPista(
            @Valid @RequestBody PistaDTO pistaDTO,
            @RequestParam(required = false) String username) {
        try {
            PistaDTO pistaCreada = pistaService.crearPista(pistaDTO, username);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.success("Pista creada exitosamente", pistaCreada));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseDTO.error("Error al crear pista: " + e.getMessage()));
        }
    }

    /**
     * PUT /api/pistas/{id}
     * Actualiza una pista existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<PistaDTO>> actualizarPista(
            @PathVariable Integer id,
            @Valid @RequestBody PistaDTO pistaDTO,
            @RequestParam(required = false) String username) {
        try {
            PistaDTO pistaActualizada = pistaService.actualizarPista(id, pistaDTO, username);
            return ResponseEntity.ok(
                ApiResponseDTO.success("Pista actualizada exitosamente", pistaActualizada)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseDTO.error("Error al actualizar pista: " + e.getMessage()));
        }
    }

    /**
     * DELETE /api/pistas/{id}
     * Elimina (desactiva) una pista
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<Void>> eliminarPista(
            @PathVariable Integer id,
            @RequestParam(required = false) String username) {
        try {
            pistaService.eliminarPista(id, username);
            return ResponseEntity.ok(
                ApiResponseDTO.success("Pista eliminada exitosamente")
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseDTO.error("Error al eliminar pista: " + e.getMessage()));
        }
    }

    /**
     * GET /api/pistas/buscar
     * Busca pistas por nombre
     */
    @GetMapping("/buscar")
    public ResponseEntity<ApiResponseDTO<List<PistaDTO>>> buscarPistas(
            @RequestParam String nombre) {
        try {
            List<PistaDTO> pistas = pistaService.buscarPorNombre(nombre);
            return ResponseEntity.ok(
                ApiResponseDTO.success("Búsqueda completada", pistas)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDTO.error("Error en la búsqueda: " + e.getMessage()));
        }
    }

    /**
     * POST /api/pistas/{id}/exito
     * Registra una completación exitosa
     */
    @PostMapping("/{id}/exito")
    public ResponseEntity<ApiResponseDTO<Void>> registrarExito(@PathVariable Integer id) {
        try {
            estadisticaService.registrarExito(id);
            return ResponseEntity.ok(
                ApiResponseDTO.success("¡Misión completada exitosamente!")
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDTO.error("Error al registrar éxito: " + e.getMessage()));
        }
    }

    /**
     * POST /api/pistas/{id}/fallo
     * Registra una completación fallida
     */
    @PostMapping("/{id}/fallo")
    public ResponseEntity<ApiResponseDTO<Void>> registrarFallo(@PathVariable Integer id) {
        try {
            estadisticaService.registrarFallo(id);
            return ResponseEntity.ok(
                ApiResponseDTO.success("Intento registrado")
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDTO.error("Error al registrar fallo: " + e.getMessage()));
        }
    }
}