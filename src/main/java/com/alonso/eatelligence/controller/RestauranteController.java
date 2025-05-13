package com.alonso.eatelligence.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alonso.eatelligence.model.entity.Plato;
import com.alonso.eatelligence.model.entity.Restaurante;
import com.alonso.eatelligence.service.IPlatoService;
import com.alonso.eatelligence.service.IRestauranteService;

import jakarta.persistence.EntityNotFoundException;

@Controller
@RequestMapping("/restaurants")
public class RestauranteController {

    @Autowired
    private IRestauranteService restauranteService;

    @Autowired
    private IPlatoService platoService;

    @GetMapping("/{id}")
    public String verPlatosRestaurante(@PathVariable Long id, Model model) {
        Restaurante restaurante = this.restauranteService.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Restaurante no encontrado"));
    
        List<Plato> platos = this.platoService.findByRestauranteId(id);
    
        platos.forEach(plato -> plato.getAlergenos().size());
    
        model.addAttribute("restaurante", restaurante);
        model.addAttribute("platos", platos);
        return "restaurante/platos";
    }
    
}
