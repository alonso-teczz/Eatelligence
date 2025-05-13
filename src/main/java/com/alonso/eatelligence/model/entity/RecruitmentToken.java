package com.alonso.eatelligence.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "recruitment_tokens")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecruitmentToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** El UUID (o aleatorio) que vas a enviar en el enlace */
    @Column(nullable = false, unique = true, length = 36)
    private String token;

    /** El username al que invitaste */
    @Column(nullable = false, length = 50)
    private String username;

    /** El rol que vas a asignarle cuando acepte */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NombreRol rol;

    /** Restaurante que crea esta invitación */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "restaurante_id", nullable = false)
    private Restaurante restaurante;

    /** Fecha y hora en que se generó el token */
    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    /** Fecha y hora de expiración (p. ej. 48h después) */
    @Column(nullable = false)
    private LocalDateTime fechaExpiracion;
}