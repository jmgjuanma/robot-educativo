package com.umg.roboteducativo.config;

import com.umg.roboteducativo.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuración de Seguridad con JWT - FASE 2
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                // Endpoints públicos (sin autenticación)
                .requestMatchers(
                    "/api/auth/**",
                    "/api/pistas/aleatoria",
                    "/api/pistas/*/exito",
                    "/api/pistas/*/fallo",
                    "/",
                    "/index.html",
                    "/login.html",
                    "/configurar.html",  // 👈 AGREGAR ESTO
                    "/admin.html",        // 👈 Y ESTO
                    "/dashboard.html",    // 👈 Y ESTO
                    "/css/**",
                    "/js/**",
                    "/assets/**",
                    "/favicon.ico",
                    "/error"
                ).permitAll()
                
                // Endpoints protegidos (requieren autenticación)
                .requestMatchers(
                    "/api/pistas",
                    "/api/pistas/**",
                    "/api/administradores/**",
                    "/api/bitacora/**",
                    "/api/estadisticas/**",
                    "/configurar.html",
                    "/admin.html",
                    "/dashboard.html"
                ).authenticated()
                
                // Cualquier otro endpoint requiere autenticación
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @SuppressWarnings("deprecation")
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}