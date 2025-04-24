package com.alonso.eatelligence.model.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlatoDTO {

    private Long id;

    @NotBlank(message = "El nombre del plato es obligatorio")
    @Size(max = 255, message = "El nombre no puede superar los 255 caracteres")
    private String nombre;

    @Size(max = 500, message = "La descripción no puede superar los 500 caracteres")
    private String descripcion;

    @Size(max = 500, message = "Los ingredientes no pueden superar los 500 caracteres")
    private String ingredientes;

    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser un número positivo")
    private Double precio;

    private Set<Long> alergenos;

}
