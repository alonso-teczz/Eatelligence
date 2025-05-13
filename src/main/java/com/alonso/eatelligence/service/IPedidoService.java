package com.alonso.eatelligence.service;

import java.time.LocalDateTime;

import com.alonso.eatelligence.model.entity.Restaurante;

public interface IPedidoService {
    long countPedidosRealizadosEntre(Restaurante restaurante, LocalDateTime desde, LocalDateTime hasta);
    long getPedidosHoy(Restaurante restaurante);
}
