package com.umg.roboteducativo.controller;

import com.umg.roboteducativo.dto.ApiResponseDTO;
import com.umg.roboteducativo.dto.CrearAdministradorDTO;
import com.umg.roboteducativo.dto.LoginRequestDTO;
import com.umg.roboteducativo.dto.LoginResponseDTO;
import com.umg.roboteducativo.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    /**
     * POST /api/auth/login
     * Autentica un usuario y devuelve un token JWT
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponseDTO<LoginResponseDTO>> login(
            @Valid @RequestBody LoginRequestDTO request,
            HttpServletRequest httpRequest) {
        try {
            String ipAddress = httpRequest.getRemoteAddr();
            LoginResponseDTO response = authService.login(request, ipAddress);
            
            return ResponseEntity.ok(
                ApiResponseDTO.success("Login exitoso", response)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponseDTO.error(e.getMessage()));
        }
    }

    /**
     * POST /api/auth/registrar
     * Registra un nuevo administrador
     */
    @PostMapping("/registrar")
    public ResponseEntity<ApiResponseDTO<LoginResponseDTO>> registrar(
            @Valid @RequestBody CrearAdministradorDTO request,
            HttpServletRequest httpRequest) {
        try {
            String ipAddress = httpRequest.getRemoteAddr();
            LoginResponseDTO response = authService.registrar(request, ipAddress);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.success("Registro exitoso", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseDTO.error(e.getMessage()));
        }
    }

    /**
     * GET /api/auth/validar
     * Valida si un token es válido
     */
    @GetMapping("/validar")
    public ResponseEntity<ApiResponseDTO<Boolean>> validarToken(
            @RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.ok(ApiResponseDTO.success("Token inválido", false));
            }

            String token = authHeader.substring(7);
            String username = authService.extraerUsername(token);
            boolean esValido = authService.validarToken(token, username);

            return ResponseEntity.ok(
                ApiResponseDTO.success("Token validado", esValido)
            );
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponseDTO.success("Token inválido", false));
        }
    }
}