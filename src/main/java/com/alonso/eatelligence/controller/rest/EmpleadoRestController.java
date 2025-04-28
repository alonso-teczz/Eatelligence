package com.alonso.eatelligence.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alonso.eatelligence.service.IUsuarioService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmpleadoRestController {

    @Autowired
    private IUsuarioService usuarioService;
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePlato(@PathVariable Long id) {
        this.usuarioService.findById(id).ifPresent(u -> {
            u.setRestauranteAsignado(null);
            u.setFechaReclutamiento(null);
        });
        return ResponseEntity.noContent().build();
    }
}
