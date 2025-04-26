// src/main/java/com/alonso/eatelligence/service/impl/PedidoServiceImp.java
package com.alonso.eatelligence.service.imp;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alonso.eatelligence.repository.IPedidoRepository;
import com.alonso.eatelligence.service.IPedidoService;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PedidoServiceImp implements IPedidoService {

    private final IPedidoRepository pedidoRepository;

    @Override
    public long countPedidosRealizadosEntre(LocalDateTime desde, LocalDateTime hasta) {
        return this.pedidoRepository.countByFechaRealizadoBetween(desde, hasta);
    }


}