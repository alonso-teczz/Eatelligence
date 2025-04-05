package com.eatelligence.model.entity;

import lombok.*;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "pedidos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "restaurante_id", nullable = false)
    private Restaurante restaurante;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPedido estado;

    @Column(nullable = false)
    private Timestamp fechaPedido;

    @Column(nullable = false)
    private Double total;

    public enum EstadoPedido {
        PENDIENTE, EN_PREPARACION, EN_CAMINO, ENTREGADO, CANCELADO
    }
}
