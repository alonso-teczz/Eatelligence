package com.alonso.eatelligence.model.entity;

import java.time.LocalDateTime;
import java.util.List;

import com.alonso.eatelligence.model.embed.Direccion;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, unique = true)
    private String email;

    private String telefono;

    @Embedded
    private Direccion direccion;

    @Column(nullable = false)
    private String contrasena;

    @ManyToMany
    @JoinTable(
        name = "usuario_rol",
        joinColumns = @JoinColumn(name = "usuario_id"),
        inverseJoinColumns = @JoinColumn(name = "rol_id")
    )
    @EqualsAndHashCode.Exclude
    private List<Rol> roles;

    @Column
    @Builder.Default
    private Boolean activo = true;

    @Column
    @Builder.Default
    private LocalDateTime fechaRegistro = LocalDateTime.now();

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    private List<Pedido> pedidos;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    private List<UsuarioRol> usuarioRoles;

}