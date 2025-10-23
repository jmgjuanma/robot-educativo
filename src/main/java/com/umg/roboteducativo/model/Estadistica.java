package com.umg.roboteducativo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "estadistica", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"pista_id", "fecha"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Estadistica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pista_id")
    private Pista pista;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(name = "total_visitas", nullable = false)
    private Integer totalVisitas = 0;

    @Column(name = "completaciones_exitosas", nullable = false)
    private Integer completacionesExitosas = 0;

    @Column(name = "completaciones_fallidas", nullable = false)
    private Integer completacionesFallidas = 0;

    //Metodo ejecutado antes de persistir la entidad
    @PrePersist
    protected void onCreate() {
        if (fecha == null) {
            fecha = LocalDate.now();
        }
        if (totalVisitas == null) {
            totalVisitas = 0;
        }
        if (completacionesExitosas == null) {
            completacionesExitosas = 0;
        }
        if (completacionesFallidas == null) {
            completacionesFallidas = 0;
        }
    }

    //Incrementa el contador de visitas
    public void incrementarVisitas() {
        this.totalVisitas++;
    }

    //Incrementa el contador de completaciones exitosas
    public void incrementarExitos() {
        this.completacionesExitosas++;
    }

    //Incrementa el contador de completaciones fallidas
    public void incrementarFallos() {
        this.completacionesFallidas++;
    }

    //Calcula el porcentaje de éxito
     //@return porcentaje de éxito (0-100)
    public double calcularPorcentajeExito() {
        int totalCompletaciones = completacionesExitosas + completacionesFallidas;
        if (totalCompletaciones == 0) {
            return 0.0;
        }
        return (completacionesExitosas * 100.0) / totalCompletaciones;
    }
}