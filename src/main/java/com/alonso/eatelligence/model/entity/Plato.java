package com.alonso.eatelligence.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "platos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Plato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(length = 500)
    private String descripcion;

    @Column(nullable = false)
    private Double precio;

    @Column(length = 500)
    private String ingredientes;

    @ManyToMany
    @JoinTable(
        name = "plato_alergenos",
        joinColumns = @JoinColumn(name = "plato_id"),
        inverseJoinColumns = @JoinColumn(name = "alergeno_id")
    )
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private Set<Alergeno> alergenos = new HashSet<>();

    @ManyToOne(optional = false)
    @JoinColumn(name = "restaurante_id")
    @EqualsAndHashCode.Exclude
    private Restaurante restaurante;
}