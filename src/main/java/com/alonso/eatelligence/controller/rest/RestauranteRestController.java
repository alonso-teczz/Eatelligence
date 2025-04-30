package com.alonso.eatelligence.controller.rest;

import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.alonso.eatelligence.model.dto.HorarioDTO;
import com.alonso.eatelligence.model.entity.Restaurante;
import com.alonso.eatelligence.service.IRestauranteService;
import com.fasterxml.jackson.databind.JsonNode;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/restaurant")
public class RestauranteRestController {
    
    @Autowired
    private IRestauranteService restauranteService;

    /**
     * GET  /api/restaurant/schedule
     * Ya no usamos @SessionAttribute("restaurante") Restaurante,
     * sino solo el ID en sesión para evitar entidades detached.
     */
    @GetMapping("/schedule")
    public ResponseEntity<Map<String, Set<HorarioDTO>>> getSchedule(
            @SessionAttribute("restauranteId") Long restauranteId
    ) {
        Set<HorarioDTO> horarios = restauranteService.obtenerHorarios(restauranteId);
        return ResponseEntity.ok(Map.of("data", horarios));
    }

    /**
     * PUT /api/restaurant/schedule
     * Actualiza los horarios recibiendo el mismo ID en sesión.
     */
    @PutMapping("/schedule")
    public ResponseEntity<Void> updateSchedule(
            @SessionAttribute("restauranteId") Long restauranteId,
            @RequestBody @Valid Set<HorarioDTO> nuevoHorario
    ) {
        restauranteService.actualizarHorarios(restauranteId, nuevoHorario);
        return ResponseEntity.noContent().build();
    }

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
