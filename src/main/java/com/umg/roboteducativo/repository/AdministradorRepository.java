package com.umg.roboteducativo.repository;

import com.umg.roboteducativo.model.Administrador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdministradorRepository extends JpaRepository<Administrador, Integer> {

    /**
     * Busca un administrador por su username
     * @param username nombre de usuario
     * @return Optional con el administrador si existe
     */
    Optional<Administrador> findByUsername(String username);

    /**
     * Busca un administrador por su email
     * @param email correo electrónico
     * @return Optional con el administrador si existe
     */
    Optional<Administrador> findByEmail(String email);

    /**
     * Verifica si existe un administrador con el username dado
     * @param username nombre de usuario
     * @return true si existe, false si no
     */
    boolean existsByUsername(String username);

    /**
     * Verifica si existe un administrador con el email dado
     * @param email correo electrónico
     * @return true si existe, false si no
     */
    boolean existsByEmail(String email);

    /**
     * Obtiene todos los administradores activos
     * @return lista de administradores activos
     */
    List<Administrador> findByActivoTrue();

    /**
     * Obtiene todos los administradores inactivos
     * @return lista de administradores inactivos
     */
    List<Administrador> findByActivoFalse();

    /**
     * Cuenta el total de administradores activos
     * @return cantidad de administradores activos
     */
    long countByActivoTrue();

    /**
     * Busca administradores por nombre (búsqueda parcial, case insensitive)
     * @param nombre nombre a buscar
     * @return lista de administradores que coinciden
     */
    @Query("SELECT a FROM Administrador a WHERE LOWER(a.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    List<Administrador> buscarPorNombre(String nombre);
}