package com.alonso.eatelligence.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "alergenos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Alergeno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private NombreAlergeno nombre;

    @Column(nullable = false)
    @Builder.Default
    private Boolean oficial = true;

    public enum NombreAlergeno {
        GLUTEN,
        CRUSTACEOS,
        HUEVOS,
        PESCADO,
        CACAHUETES,
        SOJA,
        LACTEOS,
        FRUTOS_DE_CASCARA,
        APIO,
        MOSTAZA,
        SESAMO,
        SULFITOS,
        ALTRAMUCES,
        MOLUSCOS
    }    
}