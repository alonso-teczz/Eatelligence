package com.eatelligence.model.entity;

import lombok.*;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

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

    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario cliente;

    @ManyToOne(optional = false)
    @JoinColumn(name = "restaurante_id", nullable = false)
    private Restaurante restaurante;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPedido estado;

    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pago", nullable = false)
    private MetodoPago metodoPago;

    @Column(name = "fecha_realizado", nullable = false)
    private LocalDateTime fechaRealizado;

    @Column(name = "fecha_estimada")
    private LocalDateTime fechaEstimada;

    @Column(name = "fecha_entregado")
    private LocalDateTime fechaEntregado;

    @Column(name = "nota_cliente", length = 500)
    private String notaCliente;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL)
    private List<PedidoPlato> pedidoPlatos;

    public enum EstadoPedido {
        PENDIENTE, ACEPTADO, EN_CAMINO, ENTREGADO, CANCELADO
    }

    public enum MetodoPago {
        TARJETA, PAYPAL, EFECTIVO
    }
}