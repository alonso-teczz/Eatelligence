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

    public void decrementPlato(Long restauranteId, Long platoId) {
        CartRestaurant cr = this.pedidos.get(restauranteId);
        if (cr == null) return;
        CartLine line = cr.getLineas().get(platoId);
        if (line == null) return;
        if (line.getCantidad() > 1) {
            line.setCantidad(line.getCantidad() - 1);
        } else {
            // si queda 0, quitar la línea completa
            cr.getLineas().remove(platoId);
            if (cr.getLineas().isEmpty()) {
                this.pedidos.remove(restauranteId);
            }
        }
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

    /** Número total de platos distintos en la cesta */
    public int getDistinctPlatoCount() {
        return this.pedidos.values().stream()
                   .mapToInt(cr -> cr.getLineas().size())
                   .sum();
    }

    /** Cantidad de un plato concreto en la cesta */
    public int getCantidadPlato(Long restauranteId, Long platoId) {
        CartRestaurant cr = this.pedidos.get(restauranteId);
        if (cr == null) return 0;
        CartLine line = cr.getLineas().get(platoId);
        return line != null ? line.getCantidad() : 0;
    }
    
}
