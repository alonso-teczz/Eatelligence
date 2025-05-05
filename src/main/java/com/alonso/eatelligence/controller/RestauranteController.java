package com.alonso.eatelligence.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alonso.eatelligence.model.entity.Restaurante;
import com.alonso.eatelligence.service.IRestauranteService;

@Controller
@RequestMapping("/restaurants")
public class RestauranteController {

    @Autowired
    private IRestauranteService restauranteService;

    @GetMapping("/{id}")
    public String getRestaurante(
        @PathVariable Long id,
        Model model
    ) {
        Optional<Restaurante> opt = this.restauranteService.findById(id);

        if (opt.isEmpty()) {
            return "redirect:/";
        }
        Restaurante r = opt.get();

        model.addAttribute("restaurante", r);
        model.addAttribute("platos", r.getPlatos());

        return "restaurante/platos";
    }
    
}
