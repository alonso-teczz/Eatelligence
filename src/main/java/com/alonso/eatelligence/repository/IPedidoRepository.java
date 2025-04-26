package com.alonso.eatelligence.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.alonso.eatelligence.model.entity.Pedido;

@Repository
public interface IPedidoRepository extends JpaRepository<Pedido, Long> {
    long countByFechaRealizadoBetween(LocalDateTime start, LocalDateTime end);
}