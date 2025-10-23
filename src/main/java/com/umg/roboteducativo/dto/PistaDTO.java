package com.umg.roboteducativo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PistaDTO {

    private Integer id;

    @NotBlank(message = "El nombre de la pista es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String nombre;

    @NotBlank(message = "La configuración JSON es obligatoria")
    private String configuracionJson;

    private String creadoPor; // Nombre del administrador que creó la pista

    private LocalDateTime fechaCreacion;

    private LocalDateTime fechaModificacion;

    private Boolean activa;
}