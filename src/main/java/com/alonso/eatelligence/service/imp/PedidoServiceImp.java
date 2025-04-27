// src/main/java/com/alonso/eatelligence/service/impl/PedidoServiceImp.java
package com.alonso.eatelligence.service.imp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alonso.eatelligence.model.entity.Restaurante;
import com.alonso.eatelligence.repository.IPedidoRepository;
import com.alonso.eatelligence.service.IPedidoService;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PedidoServiceImp implements IPedidoService {

    private final IPedidoRepository pedidoRepository;
    private final RestauranteServiceImp restauranteService;

    @Override
    public long countPedidosRealizadosEntre(Restaurante restaurante, LocalDateTime desde, LocalDateTime hasta) {
        return pedidoRepository.countByRestauranteAndFechaRealizadoBetween(restaurante, desde, hasta);
    }

    private final Map<Long, Long> pedidosHoyCache = new ConcurrentHashMap<>();

        /** Devuelve el nº de pedidos de HOY para un restaurante (desde la caché) */
    public long getPedidosHoy(Restaurante restaurante) {
        return pedidosHoyCache.getOrDefault(restaurante.getId(), 0L);
    }

    /* ---------- Tarea programada ---------- */

    /**  
     * Se ejecuta en el segundo 0 de cada minuto.
     * Recalcula y refresca la caché pedidosHoyCache.
     */
    @Scheduled(cron = "0 * * * * *")
    @Transactional(readOnly = true)
    public void refreshPedidosHoy() {

        LocalDateTime inicio = LocalDate.now().atStartOfDay();
        LocalDateTime fin = inicio.plusDays(1);

        this.restauranteService.findAll().forEach(r -> {
            long total = this.pedidoRepository
                .countByRestauranteAndFechaRealizadoBetween(r, inicio, fin);
            pedidosHoyCache.put(r.getId(), total);
        });
    }
}