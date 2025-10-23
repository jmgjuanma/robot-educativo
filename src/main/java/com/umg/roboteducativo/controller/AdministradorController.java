package com.umg.roboteducativo.controller;

import com.umg.roboteducativo.dto.AdministradorDTO;
import com.umg.roboteducativo.dto.ApiResponseDTO;
import com.umg.roboteducativo.dto.CrearAdministradorDTO;
import com.umg.roboteducativo.service.AdministradorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/administradores")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdministradorController {

    private final AdministradorService administradorService;

    /**
     * GET /api/administradores
     * Obtiene todos los administradores activos
     */
    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<AdministradorDTO>>> obtenerAdministradoresActivos() {
        try {
            List<AdministradorDTO> administradores = administradorService.obtenerAdministradoresActivos();
            return ResponseEntity.ok(
                ApiResponseDTO.success("Administradores obtenidos exitosamente", administradores)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDTO.error("Error al obtener administradores: " + e.getMessage()));
        }
    }

    /**
     * GET /api/administradores/todos
     * Obtiene todos los administradores (activos e inactivos)
     */
    @GetMapping("/todos")
    public ResponseEntity<ApiResponseDTO<List<AdministradorDTO>>> obtenerTodosLosAdministradores() {
        try {
            List<AdministradorDTO> administradores = administradorService.obtenerTodosLosAdministradores();
            return ResponseEntity.ok(
                ApiResponseDTO.success("Administradores obtenidos exitosamente", administradores)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDTO.error("Error al obtener administradores: " + e.getMessage()));
        }
    }

    /**
     * GET /api/administradores/{id}
     * Obtiene un administrador por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<AdministradorDTO>> obtenerAdministradorPorId(
            @PathVariable Integer id) {
        try {
            AdministradorDTO administrador = administradorService.obtenerPorId(id);
            return ResponseEntity.ok(
                ApiResponseDTO.success("Administrador encontrado", administrador)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponseDTO.error(e.getMessage()));
        }
    }

    /**
     * GET /api/administradores/username/{username}
     * Obtiene un administrador por username
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<ApiResponseDTO<AdministradorDTO>> obtenerAdministradorPorUsername(
            @PathVariable String username) {
        try {
            AdministradorDTO administrador = administradorService.obtenerPorUsername(username);
            return ResponseEntity.ok(
                ApiResponseDTO.success("Administrador encontrado", administrador)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponseDTO.error(e.getMessage()));
        }
    }

    /**
     * POST /api/administradores
     * Crea un nuevo administrador
     */
    @PostMapping
    public ResponseEntity<ApiResponseDTO<AdministradorDTO>> crearAdministrador(
            @Valid @RequestBody CrearAdministradorDTO crearDTO,
            @RequestParam(required = false) String usernameCreador) {
        try {
            AdministradorDTO adminCreado = administradorService.crearAdministrador(crearDTO, usernameCreador);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.success("Administrador creado exitosamente", adminCreado));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseDTO.error("Error al crear administrador: " + e.getMessage()));
        }
    }

    /**
     * PUT /api/administradores/{id}
     * Actualiza un administrador existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<AdministradorDTO>> actualizarAdministrador(
            @PathVariable Integer id,
            @Valid @RequestBody AdministradorDTO adminDTO,
            @RequestParam(required = false) String usernameModificador) {
        try {
            AdministradorDTO adminActualizado = administradorService.actualizarAdministrador(
                id, adminDTO, usernameModificador
            );
            return ResponseEntity.ok(
                ApiResponseDTO.success("Administrador actualizado exitosamente", adminActualizado)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseDTO.error("Error al actualizar administrador: " + e.getMessage()));
        }
    }

    /**
     * PUT /api/administradores/{id}/password
     * Cambia la contraseña de un administrador
     */
    @PutMapping("/{id}/password")
    public ResponseEntity<ApiResponseDTO<Void>> cambiarPassword(
            @PathVariable Integer id,
            @RequestParam String nuevaPassword,
            @RequestParam(required = false) String usernameModificador) {
        try {
            administradorService.cambiarPassword(id, nuevaPassword, usernameModificador);
            return ResponseEntity.ok(
                ApiResponseDTO.success("Contraseña actualizada exitosamente")
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseDTO.error("Error al cambiar contraseña: " + e.getMessage()));
        }
    }

    /**
     * DELETE /api/administradores/{id}
     * Elimina (desactiva) un administrador
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<Void>> eliminarAdministrador(
            @PathVariable Integer id,
            @RequestParam(required = false) String usernameModificador) {
        try {
            administradorService.eliminarAdministrador(id, usernameModificador);
            return ResponseEntity.ok(
                ApiResponseDTO.success("Administrador eliminado exitosamente")
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseDTO.error("Error al eliminar administrador: " + e.getMessage()));
        }
    }

    /**
     * GET /api/administradores/buscar
     * Busca administradores por nombre
     */
    @GetMapping("/buscar")
    public ResponseEntity<ApiResponseDTO<List<AdministradorDTO>>> buscarAdministradores(
            @RequestParam String nombre) {
        try {
            List<AdministradorDTO> administradores = administradorService.buscarPorNombre(nombre);
            return ResponseEntity.ok(
                ApiResponseDTO.success("Búsqueda completada", administradores)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDTO.error("Error en la búsqueda: " + e.getMessage()));
        }
    }
}