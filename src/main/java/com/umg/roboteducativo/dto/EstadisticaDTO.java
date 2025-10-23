package com.umg.roboteducativo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstadisticaDTO {

    private Integer id;

    private String pista; // Nombre de la pista

    private Integer pistaId;

    private LocalDate fecha;

    private Integer totalVisitas;

    private Integer completacionesExitosas;

    private Integer completacionesFallidas;

    private Double porcentajeExito;

    /**
     * Constructor que calcula automáticamente el porcentaje de éxito
     */
    public EstadisticaDTO(Integer id, String pista, Integer pistaId, LocalDate fecha, 
                         Integer totalVisitas, Integer completacionesExitosas, 
                         Integer completacionesFallidas) {
        this.id = id;
        this.pista = pista;
        this.pistaId = pistaId;
        this.fecha = fecha;
        this.totalVisitas = totalVisitas;
        this.completacionesExitosas = completacionesExitosas;
        this.completacionesFallidas = completacionesFallidas;
        this.porcentajeExito = calcularPorcentajeExito();
    }

    /**
     * Calcula el porcentaje de éxito
     */
    private Double calcularPorcentajeExito() {
        int totalCompletaciones = completacionesExitosas + completacionesFallidas;
        if (totalCompletaciones == 0) {
            return 0.0;
        }
        return Math.round((completacionesExitosas * 100.0 / totalCompletaciones) * 100.0) / 100.0;
    }
}