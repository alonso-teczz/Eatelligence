package com.alonso.eatelligence.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.server.ResponseStatusException;

import com.alonso.eatelligence.model.cart.Cart;
import com.alonso.eatelligence.model.cart.CartLine;
import com.alonso.eatelligence.model.entity.Plato;
import com.alonso.eatelligence.model.entity.Restaurante;
import com.alonso.eatelligence.service.IPlatoService;

@Controller
@RequestMapping("/cart")
@SessionAttributes("cart")
public class CartController {

    @Autowired
    private IPlatoService platoService;

    @ModelAttribute("cart")
    public Cart crearCarrito() {
        return new Cart();
    }

    @PostMapping("/{restauranteId}/add")
    public String addPlatoToCart(
        @PathVariable Long restauranteId,
        @RequestParam Long platoId,
        @RequestParam(defaultValue = "1") int cantidad,
        @ModelAttribute("cart") Cart cart
    ) {
        Plato plato = platoService.findById(platoId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plato no encontrado"));
    
        Restaurante restaurante = plato.getRestaurante();
        CartLine linea = new CartLine(plato, cantidad);
        cart.addPlato(restauranteId, linea, restaurante);
    
        return "redirect:/restaurants/" + restauranteId;
    }

    @PostMapping("/{restauranteId}/decrement")
    public String decrementOneUnit(
        @PathVariable Long restauranteId,
        @RequestParam Long platoId,
        @ModelAttribute("cart") Cart cart
    ) {
        cart.decrementPlato(restauranteId, platoId);
        return "redirect:/restaurants/" + restauranteId;
    }
    
    @PostMapping("/{restauranteId}/remove")
    public String removeOne(
        @PathVariable Long restauranteId,
        @RequestParam Long platoId,
        @ModelAttribute("cart") Cart cart
    ) {
        cart.removePlato(restauranteId, platoId);
        return "redirect:/restaurants/" + restauranteId;
    }

    @PostMapping("/clear")
    public String clearCart(
        @ModelAttribute("cart") Cart cart
    ) {
        cart.clear();
        return "redirect:/";
    }

}
