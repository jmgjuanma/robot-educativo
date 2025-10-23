package com.umg.roboteducativo.security;

import com.umg.roboteducativo.model.Administrador;
import com.umg.roboteducativo.repository.AdministradorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final AdministradorRepository administradorRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Administrador admin = administradorRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        // Verificar si está activo
        if (!admin.getActivo()) {
            throw new UsernameNotFoundException("Usuario inactivo: " + username);
        }

        // Crear UserDetails con rol ADMIN
        return User.builder()
                .username(admin.getUsername())
                .password(admin.getPassword())
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(!admin.getActivo())
                .build();
    }

    /**
     * Obtiene el administrador por username
     */
    public Administrador getAdministrador(String username) {
        return administradorRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
    }
}