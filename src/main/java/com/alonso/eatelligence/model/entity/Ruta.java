package com.alonso.eatelligence.model.entity;

import lombok.*;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "rutas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ruta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer tiempoEstimado;

    @Column
    private Double latitud;

    @Column
    private Double longitud;

    @Column
    private LocalDateTime horaInicio;

    @Column
    private LocalDateTime horaFin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoRuta estado;

    public enum EstadoRuta {
        ASIGNADO, EN_RUTA, TERMINADO
    }

    @OneToOne
    @JoinColumn(name = "pedido_id", unique = true, nullable = false)
    @EqualsAndHashCode.Exclude
    private Pedido pedido;

    @ManyToOne
    @JoinColumn(name = "repartidor_id", nullable = false)
    @EqualsAndHashCode.Exclude
    private Usuario repartidor;
}
