package com.eatelligence.model.entity;

import lombok.*;

import jakarta.persistence.*;

@Entity
@Table(name = "opciones_menu")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OpcionesMenu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_rol", nullable = false)
    private Rol rol;

    @Column(nullable = false, length = 100)
    private String nombreOpcion;

    @Column(nullable = false, length = 255)
    private String rutaOpcion;

    @Column(nullable = false, length = 50)
    private String seccion;

    @Column(nullable = false)
    private Boolean activo;
}
