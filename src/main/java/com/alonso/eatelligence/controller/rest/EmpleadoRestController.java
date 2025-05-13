package com.alonso.eatelligence.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alonso.eatelligence.model.entity.NombreRol;
import com.alonso.eatelligence.model.entity.Rol;
import com.alonso.eatelligence.service.IRolService;
import com.alonso.eatelligence.service.IUsuarioService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmpleadoRestController {

    @Autowired
    private IUsuarioService usuarioService;

    @Autowired
    private IRolService rolService;

    @DeleteMapping("/cook/{id}")
    public ResponseEntity<?> deleteCocinero(@PathVariable Long id) {
        this.usuarioService.findById(id).ifPresent(u -> {
            u.setRestauranteAsignado(null);
            u.setFechaReclutamiento(null);
            Rol rolCocinero = this.rolService.findByNombre(NombreRol.COCINERO)
                .orElseThrow(() -> new IllegalStateException("Rol COCINERO no existe"));
            u.getRoles().removeIf(ur -> ur.getRol().equals(rolCocinero));
            this.usuarioService.save(u);
        });
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/deliveryman/{id}")
    public ResponseEntity<?> deleteRepartidor(@PathVariable Long id) {
        this.usuarioService.findById(id).ifPresent(u -> {
            u.setRestauranteAsignado(null);
            u.setFechaReclutamiento(null);
            Rol rolRepartidor = this.rolService.findByNombre(NombreRol.REPARTIDOR)
                .orElseThrow(() -> new IllegalStateException("Rol REPARTIDOR no existe"));

            u.getRoles().removeIf(ur -> ur.getRol().equals(rolRepartidor));
            this.usuarioService.save(u);
        });
        return ResponseEntity.noContent().build();
    }
}
