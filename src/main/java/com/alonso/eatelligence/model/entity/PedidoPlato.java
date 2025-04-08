package com.alonso.eatelligence.model.entity;

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
    
    @Column(nullable = false)
    private Integer cantidad;

    //+ Para poder hacer cálculos históricos
    @Column(nullable = false)
    private Double precioUnitario;

    @ManyToOne
    @JoinColumn(name = "pedido_id", nullable = false)
    @EqualsAndHashCode.Exclude
    private Pedido pedido;

    @ManyToOne
    @JoinColumn(name = "plato_id", nullable = false)
    @EqualsAndHashCode.Exclude
    private Plato plato;
    
}