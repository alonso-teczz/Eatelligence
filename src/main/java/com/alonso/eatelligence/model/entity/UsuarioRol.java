package com.alonso.eatelligence.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "usuarios_roles",
    uniqueConstraints = @UniqueConstraint(
        name  = "uk_usuario_rol_unico",
        columnNames = {"usuario_id", "rol_id"}
    )
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"usuario", "rol"})
public class UsuarioRol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @EqualsAndHashCode.Include
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "usuario_id",
        foreignKey = @ForeignKey(name = "fk_usuario_rol_usuario")
    )
    private Usuario usuario;

    @EqualsAndHashCode.Include
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "rol_id",
        foreignKey = @ForeignKey(name = "fk_usuario_rol_rol")
    )
    private Rol rol;
}
