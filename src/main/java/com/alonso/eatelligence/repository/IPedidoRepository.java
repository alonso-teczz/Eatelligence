package com.alonso.eatelligence.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.alonso.eatelligence.model.entity.Pedido;
import com.alonso.eatelligence.model.entity.Restaurante;

@Repository
public interface IPedidoRepository extends JpaRepository<Pedido, Long> {
    long countByRestauranteAndFechaRealizadoBetween(Restaurante restaurante, LocalDateTime start, LocalDateTime end);
    @Query("""
        SELECT COALESCE(SUM(pp.cantidad), 0)
        FROM PedidoPlato pp
        WHERE pp.plato.id = :platoId
        AND pp.pedido.fechaRealizado BETWEEN :start AND :end
    """)
    int sumCantidadByPlatoIdAndFechaRealizadoBetween(
        @Param("platoId") Long platoId,
        @Param("start")   LocalDateTime start,
        @Param("end")     LocalDateTime end
    );
}