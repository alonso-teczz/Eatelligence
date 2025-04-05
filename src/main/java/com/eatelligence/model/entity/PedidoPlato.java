package com.eatelligence.model.entity;

import lombok.*;

import jakarta.persistence.*;

@Entity
@Table(name = "pedido_platos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PedidoPlato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    @ManyToOne
    @JoinColumn(name = "plato_id", nullable = false)
    private Plato plato;

    @Column(nullable = false)
    private Integer cantidad;
}
