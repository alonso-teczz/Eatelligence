package com.alonso.eatelligence.controller.rest;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.alonso.eatelligence.model.dto.CategoriaDTO;
import com.alonso.eatelligence.model.dto.HorarioDTO;
import com.alonso.eatelligence.model.entity.Restaurante;
import com.alonso.eatelligence.model.projection.ResumenProjection;
import com.alonso.eatelligence.service.IRestauranteService;
import com.fasterxml.jackson.databind.JsonNode;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/restaurant")
public class RestauranteRestController {

    @Autowired
    private IRestauranteService restauranteService;

    @GetMapping
    public Page<ResumenProjection> goIndex(
        @RequestParam(required = false) String nombre,
        @RequestParam(required = false) Double min,
        @RequestParam(required = false) Double max,
        @RequestParam double lat,
        @RequestParam double lon,
        @RequestParam(required = false) Integer radio,
        @RequestParam(required = false, name = "excluirAlergenos") Set<Long> alergenos,
        @RequestParam(required = false)Set<Long> categorias,
        Pageable pageable
    ) {
        return this.restauranteService
            .getAllRestaurantsWithFilters(nombre, min, max, lat, lon, radio, alergenos, categorias, pageable);
    }
    
    /**
     * GET  /api/restaurant/schedule
     * Ya no usamos @SessionAttribute("restaurante") Restaurante,
     * sino solo el ID en sesión para evitar entidades detached.
     */
    @GetMapping("/schedule")
    public ResponseEntity<Map<String, Set<HorarioDTO>>> getSchedule(
            @SessionAttribute("restauranteId") Long restauranteId
    ) {
        Set<HorarioDTO> horarios = this.restauranteService.obtenerHorarios(restauranteId);
        return ResponseEntity.ok(Map.of("data", horarios));
    }

    @GetMapping("/{id}/categories")
    public ResponseEntity<?> getCategorias(@PathVariable Long id) {
        try {
            List<CategoriaDTO> categorias = this.restauranteService.getCategoriasFromRestaurante(id);
            return ResponseEntity.ok(categorias);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @PatchMapping("/categories")
    @ResponseBody
    public ResponseEntity<?> patchCategorias(
        @RequestBody List<Long> ids,
        @SessionAttribute("restaurante") Restaurante restaurante
    ) {
        try {
            this.restauranteService.actualizarCategorias(restaurante, ids);
            return ResponseEntity.ok(Map.of("message", "Categorías actualizadas"));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Error al actualizar las categorías"));
        }
    }

    /**
     * PUT /api/restaurant/schedule
     * Actualiza los horarios recibiendo el mismo ID en sesión.
     */
    @PutMapping("/schedule")
    public ResponseEntity<Void> updateHorario(
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
    public ResponseEntity<?> patchActivo(
        @RequestBody JsonNode json,
        @SessionAttribute("restaurante") Restaurante restaurante
    ) {
        if (json.has("activo")) {
            boolean activo = json.get("activo").asBoolean();
            restaurante.setActivo(activo);
            this.restauranteService.save(restaurante);
        }
        
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/preptime")
    public ResponseEntity<?> patchTiempoPrepEstimado(
        @RequestBody JsonNode json,
        @SessionAttribute("restaurante") Restaurante restaurante
    ) {
        if (json.has("tiempoPreparacion")) {
            JsonNode node = json.get("tiempoPreparacion");
    
            Integer minutos = node.asInt();
            if (minutos == null || minutos < 10 || minutos > 60) {
                return ResponseEntity.badRequest().body("Introduce un número de minutos positivo entre 10 y 60");
            }

            restaurante.setTiempoPreparacionEstimado(minutos);
            restauranteService.save(restaurante);
        }
    
        return ResponseEntity.ok(restaurante.getTiempoPreparacionEstimado());
    }
    
}
