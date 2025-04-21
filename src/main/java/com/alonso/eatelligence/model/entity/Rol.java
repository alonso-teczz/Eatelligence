package com.alonso.eatelligence.model.entity;

import java.util.List;
import java.util.ArrayList;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"usuarios", "opciones", "usuarioRoles"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private NombreRol nombre;

    @ManyToMany(mappedBy = "roles")
    @EqualsAndHashCode.Exclude
    private List<Usuario> usuarios;

    @ManyToMany
    @JoinTable(
        name = "rol_opciones",
        joinColumns = @JoinColumn(name = "rol_id"),
        inverseJoinColumns = @JoinColumn(name = "opcion_id")
    )
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private List<OpcionMenu> opciones = new ArrayList<>();

    @OneToMany(mappedBy = "rol", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    private List<UsuarioRol> usuarioRoles;

    public enum NombreRol {
        ADMIN,
        REPARTIDOR,
        COCINERO,
        CLIENTE
    }
}