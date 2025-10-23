package com.umg.roboteducativo.repository;

import com.umg.roboteducativo.model.Pista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PistaRepository extends JpaRepository<Pista, Integer> {

    /**
     * Busca una pista por su nombre
     * @param nombre nombre de la pista
     * @return Optional con la pista si existe
     */
    Optional<Pista> findByNombre(String nombre);

    /**
     * Verifica si existe una pista con el nombre dado
     * @param nombre nombre de la pista
     * @return true si existe, false si no
     */
    boolean existsByNombre(String nombre);

    /**
     * Obtiene todas las pistas activas
     * @return lista de pistas activas
     */
    List<Pista> findByActivaTrue();

    /**
     * Obtiene todas las pistas inactivas
     * @return lista de pistas inactivas
     */
    List<Pista> findByActivaFalse();

    /**
     * Cuenta el total de pistas activas
     * @return cantidad de pistas activas
     */
    long countByActivaTrue();

    /**
     * Obtiene una pista aleatoria de las activas
     * Este método es crítico para el proyecto: carga aleatoriamente una pista
     * @return Optional con una pista aleatoria
     */
    @Query(value = "SELECT * FROM pista WHERE activa = true ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Optional<Pista> obtenerPistaAleatoria();

    /**
     * Busca pistas por nombre (búsqueda parcial, case insensitive)
     * @param nombre nombre a buscar
     * @return lista de pistas que coinciden
     */
    @Query("SELECT p FROM Pista p WHERE LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    List<Pista> buscarPorNombre(String nombre);

    /**
     * Obtiene las pistas creadas por un administrador específico
     * @param administradorId ID del administrador
     * @return lista de pistas creadas por el administrador
     */
    @Query("SELECT p FROM Pista p WHERE p.creadoPor.id = :administradorId")
    List<Pista> findByCreadoPorId(Integer administradorId);

    /**
     * Obtiene las últimas N pistas creadas
     * @param limite cantidad de pistas a obtener
     * @return lista de pistas ordenadas por fecha de creación descendente
     */
    @Query("SELECT p FROM Pista p ORDER BY p.fechaCreacion DESC")
    List<Pista> findUltimasPistas(int limite);
}