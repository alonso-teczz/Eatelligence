package com.alonso.eatelligence.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.alonso.eatelligence.model.cart.Cart;
import com.alonso.eatelligence.model.entity.Direccion;
import com.alonso.eatelligence.model.entity.Usuario;
import com.alonso.eatelligence.service.IDireccionService;
import com.alonso.eatelligence.utils.GeoUtils;

@Controller
@RequestMapping("/orders")
public class PedidoController {

    @Autowired
    private IDireccionService direccionService;
    
    @GetMapping("/checkout")
    public String resumenCompra(
        @SessionAttribute("cart") Cart cart,
        @SessionAttribute("usuario") Usuario usuario,
        Model model
    ) {
        // obtenemos todas las direcciones del usuario
        List<Direccion> todas = direccionService.findAllByUsuario(usuario);

        // filtramos dejando solo las que estén como mucho a 15 km
        List<Direccion> validAddresses = todas.stream()
        .filter(d -> 
        // para cada dirección d, comprobamos que **todos** los restaurantes del carrito
        // estén a ≤ 15 km de esa dirección
        cart.getPedidos().values().stream()
            .allMatch(cr -> {
            Direccion restDir = cr.getRestaurante().getDireccion();
            double km = GeoUtils.haversine(
                d.getLatitud(), d.getLongitud(),
                restDir.getLatitud(), restDir.getLongitud()
            );
            return km <= 15;
            })
        )
        .collect(Collectors.toList());
    
        model.addAttribute("cart", cart);
        model.addAttribute("usuario", usuario);
        model.addAttribute("validAddresses", validAddresses);
        return "checkout";
    }
}
