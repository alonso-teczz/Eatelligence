package com.alonso.eatelligence.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.server.ResponseStatusException;

import com.alonso.eatelligence.model.cart.Cart;
import com.alonso.eatelligence.model.cart.CartLine;
import com.alonso.eatelligence.model.entity.Plato;
import com.alonso.eatelligence.model.entity.Restaurante;
import com.alonso.eatelligence.service.IPlatoService;
import com.alonso.eatelligence.utils.ThymeleafViewRenderer;

@RestController
@RequestMapping("/cart")
@SessionAttributes("cart")
public class CartController {

    @Autowired
    private IPlatoService platoService;

    @Autowired
    private ThymeleafViewRenderer thymeleafViewRenderer; 

    @PostMapping("/{restauranteId}/add")
    public ResponseEntity<String> addPlatoToCart(
        @PathVariable Long restauranteId,
        @RequestParam Long platoId,
        @RequestParam(defaultValue = "1") int cantidad,
        @ModelAttribute("cart") Cart cart,
        Model model
    ) {
        Plato plato = this.platoService.findById(platoId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plato no encontrado"));

        Restaurante restaurante = plato.getRestaurante();

        CartLine linea = new CartLine(plato, cantidad);
        cart.addPlato(restauranteId, linea, restaurante);

        model.addAttribute("cart", cart);

        String html = this.thymeleafViewRenderer.renderFragment("fragments/home/cart", "cart", model.asMap());
        return ResponseEntity.ok(html);
    }

}
