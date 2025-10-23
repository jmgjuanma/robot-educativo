package com.umg.roboteducativo.repository;

import com.umg.roboteducativo.model.Estadistica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EstadisticaRepository extends JpaRepository<Estadistica, Integer> {

    /**
     * Busca la estadística de una pista en una fecha específica
     * @param pistaId ID de la pista
     * @param fecha fecha a buscar
     * @return Optional con la estadística si existe
     */
    @Query("SELECT e FROM Estadistica e WHERE e.pista.id = :pistaId AND e.fecha = :fecha")
    Optional<Estadistica> findByPistaIdAndFecha(
        @Param("pistaId") Integer pistaId, 
        @Param("fecha") LocalDate fecha
    );

    /**
     * Obtiene todas las estadísticas de una pista
     * @param pistaId ID de la pista
     * @return lista de estadísticas de la pista
     */
    @Query("SELECT e FROM Estadistica e WHERE e.pista.id = :pistaId ORDER BY e.fecha DESC")
    List<Estadistica> findByPistaId(@Param("pistaId") Integer pistaId);

    /**
     * Obtiene las estadísticas en un rango de fechas
     * @param fechaInicio fecha de inicio
     * @param fechaFin fecha de fin
     * @return lista de estadísticas en el rango
     */
    @Query("SELECT e FROM Estadistica e WHERE e.fecha BETWEEN :fechaInicio AND :fechaFin ORDER BY e.fecha DESC")
    List<Estadistica> findByFechaBetween(
        @Param("fechaInicio") LocalDate fechaInicio, 
        @Param("fechaFin") LocalDate fechaFin
    );

    /**
     * Obtiene las estadísticas de una pista en un rango de fechas
     * @param pistaId ID de la pista
     * @param fechaInicio fecha de inicio
     * @param fechaFin fecha de fin
     * @return lista de estadísticas
     */
    @Query("SELECT e FROM Estadistica e WHERE e.pista.id = :pistaId AND e.fecha BETWEEN :fechaInicio AND :fechaFin ORDER BY e.fecha DESC")
    List<Estadistica> findByPistaIdAndFechaBetween(
        @Param("pistaId") Integer pistaId,
        @Param("fechaInicio") LocalDate fechaInicio, 
        @Param("fechaFin") LocalDate fechaFin
    );

    /**
     * Calcula el total de visitas de todas las pistas
     * @return suma total de visitas
     */
    @Query("SELECT COALESCE(SUM(e.totalVisitas), 0) FROM Estadistica e")
    Long calcularTotalVisitas();

    /**
     * Calcula el total de completaciones exitosas
     * @return suma total de éxitos
     */
    @Query("SELECT COALESCE(SUM(e.completacionesExitosas), 0) FROM Estadistica e")
    Long calcularTotalExitos();

    /**
     * Calcula el total de completaciones fallidas
     * @return suma total de fallos
     */
    @Query("SELECT COALESCE(SUM(e.completacionesFallidas), 0) FROM Estadistica e")
    Long calcularTotalFallos();

    /**
     * Obtiene el resumen de estadísticas por pista
     * @return lista de arrays [pista_nombre, total_visitas, total_exitos, total_fallos]
     */
    @Query("SELECT p.nombre, " +
           "COALESCE(SUM(e.totalVisitas), 0), " +
           "COALESCE(SUM(e.completacionesExitosas), 0), " +
           "COALESCE(SUM(e.completacionesFallidas), 0) " +
           "FROM Pista p " +
           "LEFT JOIN Estadistica e ON p.id = e.pista.id " +
           "GROUP BY p.id, p.nombre " +
           "ORDER BY SUM(e.totalVisitas) DESC")
    List<Object[]> obtenerResumenPorPista();

    /**
     * Obtiene las estadísticas de hoy
     * @return lista de estadísticas de hoy
     */
    @Query("SELECT e FROM Estadistica e WHERE e.fecha = CURRENT_DATE")
    List<Estadistica> findEstadisticasHoy();

    /**
     * Obtiene las pistas más visitadas (top N)
     * @param limite cantidad de pistas
     * @return lista de arrays [pista_id, pista_nombre, total_visitas]
     */
    @Query(value = "SELECT p.id, p.nombre, COALESCE(SUM(e.total_visitas), 0) as visitas " +
                   "FROM pista p " +
                   "LEFT JOIN estadistica e ON p.id = e.pista_id " +
                   "GROUP BY p.id, p.nombre " +
                   "ORDER BY visitas DESC " +
                   "LIMIT :limite", nativeQuery = true)
    List<Object[]> obtenerPistasMasVisitadas(@Param("limite") int limite);

    /**
     * Obtiene las pistas con mejor tasa de éxito
     * @param limite cantidad de pistas
     * @return lista de arrays [pista_nombre, porcentaje_exito]
     */
    @Query(value = "SELECT p.nombre, " +
                   "CASE WHEN (SUM(e.completaciones_exitosas) + SUM(e.completaciones_fallidas)) > 0 " +
                   "THEN (SUM(e.completaciones_exitosas) * 100.0) / (SUM(e.completaciones_exitosas) + SUM(e.completaciones_fallidas)) " +
                   "ELSE 0 END as porcentaje_exito " +
                   "FROM pista p " +
                   "LEFT JOIN estadistica e ON p.id = e.pista_id " +
                   "GROUP BY p.id, p.nombre " +
                   "ORDER BY porcentaje_exito DESC " +
                   "LIMIT :limite", nativeQuery = true)
    List<Object[]> obtenerPistasConMejorTasaExito(@Param("limite") int limite);
}