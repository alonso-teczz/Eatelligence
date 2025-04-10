package com.alonso.eatelligence.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteRegistroDTO {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Introduce un correo válido")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[\\W_]).{8,}$",
        message = "La contraseña debe tener mínimo 8 caracteres, incluyendo mayúscula, minúscula, número y símbolo"
    )
    private String contrasena;
    
    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(
        regexp = "^\\d{9}$",
        message = "El teléfono debe tener 9 dígitos numéricos"
    )
    private String telefono;

    @NotBlank(message = "La calle es obligatoria")
    private String calle;

    @NotBlank(message = "El número de la calle es obligatorio")
    private String numCalle;

    @NotBlank(message = "La ciudad es obligatoria")
    private String ciudad;

    @NotBlank(message = "La provincia es obligatoria")
    private String provincia;

    @NotBlank(message = "El código postal es obligatorio")
    private String codigoPostal;
}