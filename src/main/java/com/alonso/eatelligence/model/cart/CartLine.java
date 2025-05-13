package com.alonso.eatelligence.model.cart;

import com.alonso.eatelligence.model.entity.Plato;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartLine {
    private Plato plato;
    private int cantidad;
}
