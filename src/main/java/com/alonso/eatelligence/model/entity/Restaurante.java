package com.alonso.eatelligence.model.entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

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

    @OneToOne(optional = false)
    @JoinColumn(name = "usuario_id", unique = true)
    @EqualsAndHashCode.Exclude
    private Usuario propietario;

    @Column(length = 500)
    private String descripcion;

    @Column
    private String telefonoFijo;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "direccion_id", nullable = false)
    private Direccion direccion;

    @ElementCollection
    @CollectionTable(name = "horarios", joinColumns = @JoinColumn(name = "restaurante_id"))
    private List<Horario> horarios;

    @OneToMany(mappedBy = "restaurante", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    private List<Plato> platos;
}
