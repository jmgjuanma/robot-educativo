package com.umg.roboteducativo.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.umg.roboteducativo.dto.CrearAdministradorDTO;
import com.umg.roboteducativo.dto.LoginRequestDTO;
import com.umg.roboteducativo.dto.LoginResponseDTO;
import com.umg.roboteducativo.model.Administrador;
import com.umg.roboteducativo.repository.AdministradorRepository;
import com.umg.roboteducativo.security.CustomUserDetailsService;
import com.umg.roboteducativo.security.JwtService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;
    private final AdministradorRepository administradorRepository;
    private final PasswordEncoder passwordEncoder;
    private final BitacoraService bitacoraService;

    /**
     * Autentica un usuario y genera un token JWT
     */
    @Transactional
    public LoginResponseDTO login(LoginRequestDTO request, String ipAddress) {
        try {
            // Autenticar con Spring Security
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()));

            // Si la autenticación es exitosa, generar token
            String token = jwtService.generateToken(request.getUsername());

            // Obtener información del administrador
            Administrador admin = userDetailsService.getAdministrador(request.getUsername());

            // Registrar en bitácora
            bitacoraService.registrarAccion(
                    request.getUsername(),
                    "LOGIN",
                    "Inicio de sesión exitoso",
                    ipAddress);

            return new LoginResponseDTO(
                    token,
                    admin.getUsername(),
                    admin.getNombre(),
                    admin.getEmail());

        } catch (AuthenticationException e) {
            // Registrar intento fallido
            bitacoraService.registrarAccion(
                    request.getUsername(),
                    "LOGIN_FALLIDO",
                    "Intento de login fallido: " + e.getMessage(),
                    ipAddress);
            throw new RuntimeException("Credenciales inválidas");
        }
    }

    /**
     * Registra un nuevo administrador
     */
    @Transactional
    public LoginResponseDTO registrar(CrearAdministradorDTO request, String ipAddress) {
        // Validar que no exista el username
        if (administradorRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("El username ya existe");
        }

        // Validar que no exista el email
        if (administradorRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        // Crear nuevo administrador
        Administrador admin = new Administrador();
        admin.setUsername(request.getUsername());
        admin.setPassword(passwordEncoder.encode(request.getPassword()));
        admin.setNombre(request.getNombre());
        admin.setEmail(request.getEmail());
        admin.setActivo(true);

        administradorRepository.save(admin);

        // Registrar en bitácora
        bitacoraService.registrarAccion(
                request.getUsername(),
                "REGISTRO",
                "Nuevo administrador registrado",
                ipAddress);

        // Generar token
        String token = jwtService.generateToken(request.getUsername());

        return new LoginResponseDTO(
                token,
                admin.getUsername(),
                admin.getNombre(),
                admin.getEmail());
    }

    /**
     * Valida un token JWT
     */
    public boolean validarToken(String token, String username) {
        return jwtService.validateToken(token, username);
    }

    /**
     * Extrae el username de un token
     */
    public String extraerUsername(String token) {
        return jwtService.extractUsername(token);
    }
}