package com.alonso.eatelligence.model.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AltaPlatoDTO {

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
    @Digits(integer = 5, fraction = 2, message = "El precio debe tener un máximo de 5 dígitos enteros y 2 decimales")
    @DecimalMin(value = "0.50", message = "El precio mínimo es 0.50")
    private Double precio;

    private Set<Long> alergenos;

    @Min(value = 1, message = "El límite diario debe ser al menos 1 unidad")
    private Integer limiteUnidadesDiarias;

    private Boolean activo;
}
