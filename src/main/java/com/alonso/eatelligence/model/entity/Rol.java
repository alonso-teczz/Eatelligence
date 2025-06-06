package com.alonso.eatelligence.model.entity;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"usuarios", "opciones"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @EqualsAndHashCode.Include
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private NombreRol nombre;

    @ManyToMany(mappedBy = "roles")
    @Builder.Default
    private Set<Usuario> usuarios = new HashSet<>();

    @ManyToMany(cascade = CascadeType.MERGE)
    @JoinTable(
        name = "rol_opcion",
        joinColumns = @JoinColumn(name = "rol_id"),
        inverseJoinColumns = @JoinColumn(name = "opcion_id"),
        uniqueConstraints = @UniqueConstraint(columnNames = {"rol_id","opcion_id"})
    )
    @Builder.Default
    private Set<OpcionMenu> opciones = new HashSet<>();

}