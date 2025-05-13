package com.alonso.eatelligence.model.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "pedidos_grupo")
public class PedidoGrupo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    @EqualsAndHashCode.Exclude
    private Usuario cliente;

    @Column(nullable = false)
    private LocalDateTime fechaRealizado;

    @Column
    private LocalDateTime fechaEntregado;

    @Enumerated(EnumType.STRING)
    private MetodoPago metodoPago;

    @Enumerated(EnumType.STRING)
    private EstadoGrupo estadoGrupo;

    @OneToMany(mappedBy = "grupo", cascade = CascadeType.ALL)
    private List<Pedido> subPedidos;

    public enum MetodoPago {
        TARJETA, PAYPAL, EFECTIVO
    }

    public enum EstadoGrupo {
        PENDIENTE, COMPLETADO, CANCELADO
    }
}