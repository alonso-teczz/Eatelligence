package com.alonso.eatelligence.controller;


import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.alonso.eatelligence.model.cart.Cart;
import com.alonso.eatelligence.model.entity.Usuario;
import com.alonso.eatelligence.model.projection.ResumenProjection;
import com.alonso.eatelligence.service.IAlergenoService;
import com.alonso.eatelligence.service.ICategoriaService;
import com.alonso.eatelligence.service.IDireccionService;
import com.alonso.eatelligence.service.IRestauranteService;

@Controller
@SessionAttributes("cart")
public class HomeController {

    @Autowired
    private IRestauranteService restauranteService;

    @Autowired
    private IDireccionService direccionService;

    @Autowired
    private IAlergenoService alergenoService;

    @Autowired
    private ICategoriaService categoriaService;

    @ModelAttribute("cart")
    public Cart cart() {
        return new Cart();
    }

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
        @RequestParam(defaultValue = "9") int size,
        @RequestParam(required = false) Set<Long> categorias,
        @SessionAttribute(required = false) Usuario usuario,
        Model model
    ) {
        if (usuario != null) {
            ZoneId zone = ZoneId.of("Europe/Madrid");
            DayOfWeek dia   = LocalDate.now(zone).getDayOfWeek();
            LocalTime hora  = LocalTime.now(zone);
            
            Pageable pageable = PageRequest.of(page, size);
            Page<ResumenProjection> resultados =
                restauranteService.getAllRestaurantsWithFilters(
                    nombre, min, max, lat, lon, radio,
                    excluirAlergenos, categorias,
                    dia, hora, pageable
                );
            
            model.addAttribute("alergenos", this.alergenoService.getAll());
            model.addAttribute("categorias", this.categoriaService.getAll());
            model.addAttribute("restaurantes", resultados.getContent());
            model.addAttribute("pagina", resultados);
            model.addAttribute("direcciones", this.direccionService.getDireccionesUsuario(usuario.getId()));
        }
        
        return "index";
    }
    
    @GetMapping("/denied-access")
    public String mostrarAccesoDenegado() {
        return "error/accesoDenegado";
    }

}
