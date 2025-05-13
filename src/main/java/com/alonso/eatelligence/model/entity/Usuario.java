package com.alonso.eatelligence.model.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PreRemove;
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
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"roles", "tokens", "direcciones"})
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = true)
    private String apellidos;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String telefonoMovil;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @Builder.Default
    private Integer puntos = 0;

    @ManyToOne
    @JoinColumn(nullable = true)
    @Builder.Default
    private Restaurante restauranteAsignado = null;

    @Column(nullable = true)
    @Builder.Default
    private LocalDateTime fechaReclutamiento = null;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Direccion> direcciones = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @Builder.Default
    @JoinTable(
        name = "usuario_rol",
        joinColumns = @JoinColumn(
            name = "usuario_id",
            foreignKey = @ForeignKey(name = "fk_usuario_rol_usuario")
        ),
        inverseJoinColumns = @JoinColumn(
            name = "rol_id",
            foreignKey = @ForeignKey(name = "fk_usuario_rol_rol")
        ),
        uniqueConstraints  = @UniqueConstraint(columnNames = { "usuario_id", "rol_id" })
    )
    private Set<Rol> roles = new HashSet<>();

    @Column(nullable = false, columnDefinition = "TINYINT(1)")
    @Builder.Default
    private boolean verificado = false;

    @Column
    @Builder.Default
    private LocalDateTime fechaRegistro = LocalDateTime.now();

    @OneToMany(mappedBy = "cliente", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PedidoGrupo> pedidosGrupo = new ArrayList<>();    

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<VerificationToken> tokens = new ArrayList<>();

    @PreRemove
    private void preRemoveUsuario() {
        this.tokens.clear();
        this.roles.clear();
    }   

}