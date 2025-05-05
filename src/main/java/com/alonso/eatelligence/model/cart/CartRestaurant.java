package com.alonso.eatelligence.model.cart;

import java.util.LinkedHashMap;
import java.util.Map;

import com.alonso.eatelligence.model.entity.Plato;
import com.alonso.eatelligence.model.entity.Restaurante;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartRestaurant {
    private Restaurante restaurante;
    private Map<Long, CartLine> lineas = new LinkedHashMap<>();

    public void addPlato(Plato plato, int cantidad) {
        this.lineas.merge(
            plato.getId(),
            new CartLine(plato, cantidad),
            (oldLine, newLine) -> {
                oldLine.setCantidad(oldLine.getCantidad() + newLine.getCantidad());
                return oldLine;
            }
        );
    }

    public int getTotalPlatos() {
        return this.lineas.values().stream()
            .mapToInt(CartLine::getCantidad)
            .sum();
    }

    public double totalPrecio() {
        return this.lineas.values().stream()
            .mapToDouble(linea -> linea.getPlato().getPrecio() * linea.getCantidad())
            .sum();
    }    

    public void removePlato(Long platoId) {
        this.lineas.remove(platoId);
    }

    public void clear() {
        this.lineas.clear();
    }
}
