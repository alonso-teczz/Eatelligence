package com.alonso.eatelligence.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class RestauranteRegistroDTO extends ClienteRegistroDTO {
    @NotBlank(message = "El nombre comercial no puede estar vacío")
    @Size(min = 6, message = "El nombre comercial debe tener al menos 6 caracteres")
    private String nombreComercial;

    @NotBlank(message = "La descripción no puede estar vacía")
    @Size(min = 6, message = "La descripción debe tener al menos 6 caracteres")
    private String descripcion;
}
