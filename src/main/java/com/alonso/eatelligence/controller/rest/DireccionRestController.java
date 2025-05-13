package com.alonso.eatelligence.controller.rest;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alonso.eatelligence.model.entity.Direccion;
import com.alonso.eatelligence.service.IDireccionService;


@RestController
@RequestMapping("/api/directions")
public class DireccionRestController {

    @Autowired
    private IDireccionService direccionService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getDireccion(@PathVariable("id") Long direccionId) {
        Direccion direccion = this.direccionService.getById(direccionId).orElse(null);
        if (direccion == null) {
            return ResponseEntity.badRequest().body("Dirección no encontrada");
        }
        return ResponseEntity.ok(Map.of(
            "lat", direccion.getLatitud(),
            "lon", direccion.getLongitud()
        ));
    }
    
}
