package com.alonso.eatelligence.model.entity;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

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

    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;

    @Column(nullable = true)
    @Builder.Default
    private Integer limiteUnidadesDiarias = null;

    @ManyToMany
    @JoinTable(
        name = "plato_alergeno",
        joinColumns = @JoinColumn(name = "plato_id"),
        inverseJoinColumns = @JoinColumn(name = "alergeno_id"),
        uniqueConstraints = @UniqueConstraint(columnNames = { "plato_id", "alergeno_id" })
    )
    @Builder.Default
    @JsonIgnoreProperties("platos")
    @EqualsAndHashCode.Exclude
    private Set<Alergeno> alergenos = new HashSet<>();

    @ManyToOne(optional = false)
    @JoinColumn(name = "restaurante_id")
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private Restaurante restaurante;
}