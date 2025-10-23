package com.umg.roboteducativo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "pista")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "El nombre de la pista es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    @Column(nullable = false, unique = true, length = 100)
    private String nombre;

    @NotBlank(message = "La configuración JSON es obligatoria")
    @Column(name = "configuracion_json", nullable = false, columnDefinition = "TEXT")
    private String configuracionJson;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creado_por")
    private Administrador creadoPor;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_modificacion", nullable = false)
    private LocalDateTime fechaModificacion;

    @Column(nullable = false)
    private Boolean activa = true;

    //Metodo ejecutado antes de persistir la entidad
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        fechaCreacion = now;
        fechaModificacion = now;
        if (activa == null) {
            activa = true;
        }
    }

    //Metodo ejecutado antes de actualizar la entidad
    @PreUpdate
    protected void onUpdate() {
        fechaModificacion = LocalDateTime.now();
    }
}