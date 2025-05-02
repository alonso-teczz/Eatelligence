package com.alonso.eatelligence.controller;


import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.alonso.eatelligence.model.entity.Usuario;
import com.alonso.eatelligence.model.projection.ResumenProjection;
import com.alonso.eatelligence.service.IAlergenoService;
import com.alonso.eatelligence.service.IDireccionService;
import com.alonso.eatelligence.service.IRestauranteService;

@Controller
public class HomeController {

    @Autowired
    private IRestauranteService restauranteService;

    @Autowired
    private IDireccionService direccionService;

    @Autowired
    private IAlergenoService alergenoService;

    @GetMapping("/")
    public String goIndex(
        @RequestParam(required = false) String nombre,
        @RequestParam(required = false) Double min,
        @RequestParam(required = false) Double max,
        @RequestParam(defaultValue = "0") double lat,
        @RequestParam(defaultValue = "0") double lon,
        @RequestParam(required = false) Integer radio,
        @RequestParam(required = false) Set<Long> excluirAlergenos,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @SessionAttribute(name = "usuario", required = false) Usuario usuario,
        Model model
    ) {
        
        if (usuario != null) {
            Pageable pageable = PageRequest.of(page, size);
        
            Page<ResumenProjection> resultados = restauranteService.getAllRestaurantsWithFilters(
                nombre, min, max, lat, lon, radio, excluirAlergenos, pageable
            );
        
            model.addAttribute("alergenos", alergenoService.getAll());
            model.addAttribute("restaurantes", resultados.getContent());
            model.addAttribute("pagina", resultados);
            model.addAttribute("direcciones", direccionService.getDireccionesUsuario(usuario.getId()));
        }
    
        return "index";
    }
    

}
