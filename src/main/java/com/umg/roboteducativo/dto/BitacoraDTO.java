package com.umg.roboteducativo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BitacoraDTO {

    private Integer id;

    private String administrador; // Nombre del administrador

    private String username; // Username del administrador

    private String accion;

    private String descripcion;

    private LocalDateTime fechaHora;

    private String ipAddress;
}