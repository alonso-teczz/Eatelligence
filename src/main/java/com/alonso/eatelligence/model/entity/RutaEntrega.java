package com.alonso.eatelligence.model.entity;

import lombok.*;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "rutas_entrega")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RutaEntrega {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tiempo_estimado", nullable = false)
    private Integer tiempoEstimado;

    @Column(name = "latitud")
    private Double latitud;

    @Column(name = "longitud")
    private Double longitud;

    @Column(name = "hora_inicio")
    private LocalDateTime horaInicio;

    @Column(name = "hora_fin")
    private LocalDateTime horaFin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoRuta estado;

    public enum EstadoRuta {
        ASIGNADO, EN_RUTA, ENTREGADO
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
