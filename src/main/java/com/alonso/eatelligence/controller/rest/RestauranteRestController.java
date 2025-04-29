package com.alonso.eatelligence.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.alonso.eatelligence.model.entity.Restaurante;
import com.alonso.eatelligence.service.IRestauranteService;
import com.fasterxml.jackson.databind.JsonNode;

@RestController
@RequestMapping("/api/restaurant")
public class RestauranteRestController {
    
    @Autowired
    private IRestauranteService restauranteService;

    @PatchMapping("/amount")
    public ResponseEntity<Double> patchImporteMinimo(
        @RequestBody JsonNode json,
        @SessionAttribute("restaurante") Restaurante restaurante
    ) {
        if (json.has("importeMinimo")) {
            JsonNode node = json.get("importeMinimo");

            if (node.isNull()) {
                restaurante.setImporteMinimo(null);
            } else {
                restaurante.setImporteMinimo(node.asDouble());
            }

            restauranteService.save(restaurante);
        }

        return ResponseEntity.ok(restaurante.getImporteMinimo());
    }

    @PatchMapping("/active")
    public ResponseEntity<?> patchRestauranteActivo(
        @RequestBody JsonNode json,
        @SessionAttribute("restaurante") Restaurante restaurante) {

        if (json.has("activo")) {
            boolean activo = json.get("activo").asBoolean();
            restaurante.setActivo(activo);
            this.restauranteService.save(restaurante);
        }
        
        return ResponseEntity.noContent().build();
    }
}
