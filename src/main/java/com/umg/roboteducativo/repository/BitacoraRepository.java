package com.umg.roboteducativo.repository;

import com.umg.roboteducativo.model.Bitacora;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BitacoraRepository extends JpaRepository<Bitacora, Integer> {

    /**
     * Obtiene todas las entradas de bitácora ordenadas por fecha descendente
     * @return lista de entradas de bitácora
     */
    List<Bitacora> findAllByOrderByFechaHoraDesc();

    /**
     * Obtiene las entradas de bitácora de un administrador específico
     * @param administradorId ID del administrador
     * @return lista de entradas de bitácora del administrador
     */
    @Query("SELECT b FROM Bitacora b WHERE b.administrador.id = :administradorId ORDER BY b.fechaHora DESC")
    List<Bitacora> findByAdministradorId(@Param("administradorId") Integer administradorId);

    /**
     * Obtiene las entradas de bitácora por tipo de acción
     * @param accion tipo de acción
     * @return lista de entradas de bitácora con esa acción
     */
    List<Bitacora> findByAccionOrderByFechaHoraDesc(String accion);

    /**
     * Obtiene las entradas de bitácora en un rango de fechas
     * @param fechaInicio fecha de inicio
     * @param fechaFin fecha de fin
     * @return lista de entradas de bitácora en el rango
     */
    @Query("SELECT b FROM Bitacora b WHERE b.fechaHora BETWEEN :fechaInicio AND :fechaFin ORDER BY b.fechaHora DESC")
    List<Bitacora> findByFechaHoraBetween(
        @Param("fechaInicio") LocalDateTime fechaInicio, 
        @Param("fechaFin") LocalDateTime fechaFin
    );

    /**
     * Obtiene las últimas N entradas de la bitácora
     * @param limite cantidad de entradas
     * @return lista de las últimas entradas
     */
    @Query(value = "SELECT * FROM bitacora ORDER BY fecha_hora DESC LIMIT :limite", nativeQuery = true)
    List<Bitacora> findUltimas(@Param("limite") int limite);

    /**
     * Cuenta las acciones realizadas por un administrador
     * @param administradorId ID del administrador
     * @return cantidad de acciones
     */
    @Query("SELECT COUNT(b) FROM Bitacora b WHERE b.administrador.id = :administradorId")
    long countByAdministradorId(@Param("administradorId") Integer administradorId);

    /**
     * Busca en la bitácora por descripción (búsqueda parcial)
     * @param texto texto a buscar
     * @return lista de entradas que coinciden
     */
    @Query("SELECT b FROM Bitacora b WHERE LOWER(b.descripcion) LIKE LOWER(CONCAT('%', :texto, '%')) ORDER BY b.fechaHora DESC")
    List<Bitacora> buscarPorDescripcion(@Param("texto") String texto);

    /**
     * Obtiene estadísticas de acciones por tipo
     * @return lista de arrays [accion, cantidad]
     */
    @Query("SELECT b.accion, COUNT(b) FROM Bitacora b GROUP BY b.accion ORDER BY COUNT(b) DESC")
    List<Object[]> obtenerEstadisticasPorAccion();
}