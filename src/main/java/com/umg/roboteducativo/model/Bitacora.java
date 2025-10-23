package com.umg.roboteducativo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Entity
@Table(name = "bitacora")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bitacora {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "administrador_id")
    private Administrador administrador;

    @NotBlank(message = "La acción es obligatoria")
    @Size(max = 100, message = "La acción no puede exceder 100 caracteres")
    @Column(nullable = false, length = 100)
    private String accion;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "fecha_hora", nullable = false, updatable = false)
    private LocalDateTime fechaHora;

    @Size(max = 45, message = "La IP no puede exceder 45 caracteres")
    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    //Método ejecutado antes de persistir la entidad
    @PrePersist
    protected void onCreate() {
        fechaHora = LocalDateTime.now();
    }
}