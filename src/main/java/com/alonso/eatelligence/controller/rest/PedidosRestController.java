package com.alonso.eatelligence.controller.rest;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.alonso.eatelligence.model.entity.Restaurante;
import com.alonso.eatelligence.service.IPedidoService;


@RestController
@RequestMapping("/api/orders")
public class PedidosRestController {
    
    @Autowired
    private IPedidoService pedidoService;

    @GetMapping("/today")
    public ResponseEntity<?> getOrdersToday(@SessionAttribute("restaurante") Restaurante restaurante) {
        return ResponseEntity.ok(pedidoService.countPedidosRealizadosEntre(
            restaurante,
            LocalDate.now().atStartOfDay(),
            LocalDate.now().plusDays(1).atStartOfDay()
        ));
    }
    
}
