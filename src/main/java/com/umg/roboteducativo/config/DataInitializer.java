package com.umg.roboteducativo.config;

import com.umg.roboteducativo.model.Administrador;
import com.umg.roboteducativo.repository.AdministradorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final AdministradorRepository administradorRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Verificar si ya existe un administrador
        if (administradorRepository.count() == 0) {
            log.info("No hay administradores en la base de datos. Creando admin por defecto...");
            
            Administrador admin = new Administrador();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setNombre("Administrador Principal");
            admin.setEmail("admin@robot.edu");
            admin.setActivo(true);
            
            administradorRepository.save(admin);
            
            log.info("✅ Administrador creado exitosamente!");
            log.info("   Username: admin");
            log.info("   Password: admin123");
            log.info("   ⚠️ CAMBIA ESTA CONTRASEÑA EN PRODUCCIÓN");
        }
    }
}