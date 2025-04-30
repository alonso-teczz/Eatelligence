package com.alonso.eatelligence.model.entity;

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
    @JoinColumn(name = "restaurante_id", nullable = false)
    @EqualsAndHashCode.Exclude
    private Restaurante restaurante;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPedido estado;

    @Column(nullable = false)
    private LocalDateTime fechaRealizado;

    @Column(name = "nota_cliente", length = 500)
    private String notaCliente;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    @EqualsAndHashCode.Exclude
    private List<PedidoPlato> pedidoPlatos;

    @ManyToOne
    @JoinColumn(name = "grupo_id")
    private PedidoGrupo grupo;

    public enum EstadoPedido {
        PENDIENTE, ACEPTADO, EN_CAMINO, ENTREGADO, CANCELADO
    }

}