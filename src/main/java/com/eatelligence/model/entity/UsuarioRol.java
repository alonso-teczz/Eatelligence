package com.eatelligence.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "usuario_rol")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioRol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(optional = false)
    @JoinColumn(name = "rol_id", nullable = false)
    private Rol rol;

}
