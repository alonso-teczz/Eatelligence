package com.alonso.eatelligence.model.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "restaurantes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Restaurante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombreComercial;

    @OneToOne(optional = false, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "usuario_id", unique = true)
    @ToString.Exclude
    private Usuario propietario;    

    @Column(length = 500)
    private String descripcion;

    @Column
    private String telefonoFijo;

    @Column(nullable = false)
    private String emailEmpresa;

    @Column(nullable = true)
    @Builder.Default
    private Double importeMinimo = null;

    @Column(name = "tiempo_preparacion_estimado")
    private Integer tiempoPreparacionEstimado;

    @Column(nullable = false, columnDefinition = "TINYINT(1)")
    @Builder.Default
    private boolean verificado = false;

    @Column(nullable = false, columnDefinition = "TINYINT(1)")
    @Builder.Default
    private boolean activo = true;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "direccion_id", nullable = false)
    @ToString.Exclude
    private Direccion direccion;

    @ElementCollection
    @CollectionTable(name = "horarios", joinColumns = @JoinColumn(name = "restaurante_id"))
    @Builder.Default
    private List<Horario> horarios = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name  = "restaurante_categoria",
        joinColumns = @JoinColumn(name = "restaurante_id"),
        inverseJoinColumns = @JoinColumn(name = "categoria_id")
    )
    @Builder.Default
    private Set<Categoria> categorias = new HashSet<>();

    @OneToMany(mappedBy = "restaurante", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @Builder.Default
    private Set<Plato> platos = new HashSet<>();

    @OneToMany(mappedBy = "restaurante", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<VerificationToken> tokens;

}
