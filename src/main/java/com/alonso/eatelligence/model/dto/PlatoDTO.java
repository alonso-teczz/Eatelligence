package com.alonso.eatelligence.model.dto;

import com.alonso.eatelligence.model.entity.Plato;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlatoDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private String ingredientes;
    private Double precio;
    private String alergenos;

    public PlatoDTO(Plato plato, String alergenos) {
        this.id = plato.getId();
        this.nombre = plato.getNombre();
        this.descripcion = plato.getDescripcion();
        this.ingredientes = plato.getIngredientes();
        this.precio = plato.getPrecio();
        this.alergenos = alergenos;
    }
}
