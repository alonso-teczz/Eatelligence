package com.eatelligence.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioRegistroDTO {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Introduce un correo válido")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    private String contrasena;

    @NotBlank(message = "El teléfono es obligatorio")
    private String telefono;

    @NotBlank(message = "La calle es obligatoria")
    private String calle;

    @NotBlank(message = "La ciudad es obligatoria")
    private String ciudad;

    @NotBlank(message = "La provincia es obligatoria")
    private String provincia;

    @NotBlank(message = "El código postal es obligatorio")
    private String codigoPostal;
}
