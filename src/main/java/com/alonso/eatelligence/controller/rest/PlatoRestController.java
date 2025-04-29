package com.alonso.eatelligence.controller.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alonso.eatelligence.model.dto.PlatoDTO;
import com.alonso.eatelligence.model.entity.Plato;
import com.alonso.eatelligence.service.IPlatoService;
import com.alonso.eatelligence.utils.ValidationUtils;
import com.fasterxml.jackson.databind.JsonNode;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/plates")
@RequiredArgsConstructor
public class PlatoRestController {

    private final IPlatoService platoService;

    // Actualizar plato
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePlato(
        @PathVariable Long id,
        @Valid @RequestBody PlatoDTO platoActualizado,
        BindingResult result
    ) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(ValidationUtils.getFirstOrderedErrorFromBindingResult(result, PlatoDTO.class).get().getDefaultMessage());
        }
        
        return ResponseEntity.ok(this.platoService.updateFromDTO(id, platoActualizado));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> patchPlato(
        @PathVariable Long id,
        @RequestBody JsonNode json
    ) {
        if (json.has("activo")) {
            boolean activo = json.get("activo").asBoolean();
            Plato p = this.platoService.findyById(id);
            p.setActivo(activo);
            this.platoService.save(p);
        }
        
        return ResponseEntity.noContent().build();
    }

    // Eliminar plato
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePlato(@PathVariable Long id) {
        this.platoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
