package com.alonso.eatelligence.model.cart;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import com.alonso.eatelligence.model.entity.Restaurante;

public class Cart {

    private final Map<Long, CartRestaurant> pedidos = new LinkedHashMap<>();

    public void addPlato(Long restauranteId, CartLine linea, Restaurante restaurante) {
        this.pedidos.computeIfAbsent(restauranteId, id -> {
            CartRestaurant cr = new CartRestaurant();
            cr.setRestaurante(restaurante);
            return cr;
        }).addPlato(linea.getPlato(), linea.getCantidad());
    }

    public void removePlato(Long restauranteId, Long platoId) {
        if (this.pedidos.containsKey(restauranteId)) {
            this.pedidos.get(restauranteId).removePlato(platoId);
            if (this.pedidos.get(restauranteId).getLineas().isEmpty()) {
                this.pedidos.remove(restauranteId);
            }
        }
    }

    public void clear() {
        this.pedidos.clear();
    }

    public Map<Long, CartRestaurant> getPedidos() {
        return this.pedidos;
    }

    public int getTotalPlatos() {
        return this.pedidos.values().stream()
            .flatMap(cr -> cr.getLineas().values().stream())
            .mapToInt(CartLine::getCantidad)
            .sum();
    }

    public BigDecimal totalPrecio() {
        return this.pedidos.values().stream()
            .flatMap(cr -> cr.getLineas().values().stream())
            .map(linea -> BigDecimal.valueOf(linea.getPlato().getPrecio())
                .multiply(BigDecimal.valueOf(linea.getCantidad())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
}
