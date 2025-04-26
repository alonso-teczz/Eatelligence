package com.alonso.eatelligence.service;

import java.time.LocalDateTime;

public interface IPedidoService {
    long countPedidosRealizadosEntre(LocalDateTime desde, LocalDateTime hasta);
}
