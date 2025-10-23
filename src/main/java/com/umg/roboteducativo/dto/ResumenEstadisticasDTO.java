package com.umg.roboteducativo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResumenEstadisticasDTO {

    private Long totalVisitas;

    private Long totalExitos;

    private Long totalFallos;

    private Long totalPistasActivas;

    private Long totalAdministradores;

    private Double porcentajeExitoGlobal;

    /**
     * Constructor que calcula el porcentaje de éxito global
     */
    public ResumenEstadisticasDTO(Long totalVisitas, Long totalExitos, Long totalFallos, 
                                 Long totalPistasActivas, Long totalAdministradores) {
        this.totalVisitas = totalVisitas;
        this.totalExitos = totalExitos;
        this.totalFallos = totalFallos;
        this.totalPistasActivas = totalPistasActivas;
        this.totalAdministradores = totalAdministradores;
        this.porcentajeExitoGlobal = calcularPorcentajeExito();
    }

    /**
     * Calcula el porcentaje de éxito global
     */
    private Double calcularPorcentajeExito() {
        long totalCompletaciones = totalExitos + totalFallos;
        if (totalCompletaciones == 0) {
            return 0.0;
        }
        return Math.round((totalExitos * 100.0 / totalCompletaciones) * 100.0) / 100.0;
    }
}