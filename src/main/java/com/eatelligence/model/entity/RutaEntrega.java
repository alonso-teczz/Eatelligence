package com.eatelligence.model.entity;

import lombok.*;

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

    @OneToOne
    @JoinColumn(name = "pedido_id", unique = true, nullable = false)
    private Pedido pedido;

    @ManyToOne
    @JoinColumn(name = "repartidor_id", nullable = false)
    private Usuario repartidor;

    private String ubicacionActual;

    private Integer tiempoEstimadoEntrega;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoRuta estado;

    public enum EstadoRuta {
        ASIGNADO, EN_RUTA, ENTREGADO
    }
}
