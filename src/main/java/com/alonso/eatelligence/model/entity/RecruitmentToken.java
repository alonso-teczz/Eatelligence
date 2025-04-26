package com.alonso.eatelligence.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import com.alonso.eatelligence.model.entity.Rol.NombreRol;

@Entity
@Table
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

    /** Fecha y hora en que se generó el token */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime fechaCreacion;

    /** Fecha y hora de expiración (p. ej. 48h después) */
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime fechaExpiracion;

}
