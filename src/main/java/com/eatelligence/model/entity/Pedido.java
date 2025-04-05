package com.eatelligence.model.entity;

import lombok.*;

import jakarta.persistence.*;
import java.time.LocalDateTime;

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
    
    @Column(nullable = false)
    private LocalDateTime fechaPedido;

    @Column(nullable = false)
    private LocalDateTime fechaEntregaEstimada;

    @Column(nullable = false)
    private LocalDateTime fechaEntregado;

    @Column(nullable = true)
    private LocalDateTime fechaCancelado;

    @Column(nullable = false)
    private Double total;

    @Column(name = "nota_cliente", length = 500)
    private String notaCliente;

    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pago", nullable = false)
    private MetodoPago metodoPago;

    public enum MetodoPago {
        EFECTIVO,
        TARJETA,
        PAYPAL,
        BIZUM
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPedido estado;

    public enum EstadoPedido {
        PENDIENTE, EN_PREPARACION, EN_CAMINO, ENTREGADO, CANCELADO
    }

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "restaurante_id", nullable = false)
    private Restaurante restaurante;    

}
