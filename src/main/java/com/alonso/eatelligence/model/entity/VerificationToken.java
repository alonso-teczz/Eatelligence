package com.alonso.eatelligence.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private LocalDateTime fechaExpiracion;

    @Column
    @Builder.Default
    private Integer intentosReenvio = 0;
    
    @Column
    private LocalDateTime ultimoIntento;    

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoVerificacion tipo;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = true, foreignKey = @ForeignKey(
        name = "FK_verification_token_usuario",
        foreignKeyDefinition = "FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE"
    ))
    private Usuario usuario;

    @OneToOne
    @JoinColumn(name = "restaurante_id", nullable = true)
    private Restaurante restaurante;

    public enum TipoVerificacion {
        USUARIO,
        RESTAURANTE
    }
}