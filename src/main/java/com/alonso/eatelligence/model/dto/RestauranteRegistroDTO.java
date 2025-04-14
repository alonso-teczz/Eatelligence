package com.alonso.eatelligence.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestauranteRegistroDTO {

    @Valid
    private ClienteRegistroDTO propietario;

    @Valid
    @NotNull(message = "La dirección del restaurante es obligatoria")
    private DireccionRegistroDTO direccionRestaurante;

    @NotBlank(message = "El nombre comercial no puede estar vacío")
    @Size(min = 6, max = 50, message = "El nombre comercial debe tener al menos 6 caracteres")
    private String nombreComercial;

    @NotBlank(message = "La descripción no puede estar vacía")
    @Size(min = 6, message = "La descripción debe tener al menos 6 caracteres")
    private String descripcion;

    @Pattern(
        regexp = "^$|^[89]\\d{8}$",
        message = "El teléfono móvil debe tener 9 dígitos y empezar por 8 ó 9"
    )
    private String telefonoFijo;

    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "Introduce un correo válido")
    private String emailEmpresa;
}
