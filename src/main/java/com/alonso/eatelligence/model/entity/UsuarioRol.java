package com.alonso.eatelligence.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "usuario_rol",
       uniqueConstraints = @UniqueConstraint(columnNames = { "usuario_id", "rol_id" }))
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class UsuarioRol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id", foreignKey = @ForeignKey(name = "fk_usuario_rol_usuario"))
    private Usuario usuario;

    @ManyToOne(optional = false)
    @JoinColumn(name = "rol_id", foreignKey = @ForeignKey(name = "fk_usuario_rol_rol"))
    private Rol rol;
}
