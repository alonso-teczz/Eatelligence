package com.alonso.eatelligence.model.dto;

import com.alonso.eatelligence.validation.annotations.PasswordMatches;
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
@PasswordMatches
public class ClienteRegistroDTO {

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(min = 6, max = 20, message = "El nombre de usuario debe tener de 6 a 20 caracteres")
    private String username;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 20, message = "El nombre debe tener de 3 a 20 caracteres")
    private String nombre;

    @NotBlank(message = "Los apellidos son obligatorios")
    @Size(min = 6, max = 30, message = "Los apellidos deben tener de 6 a 30 caracteres")
    private String apellidos;

    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "Introduce un correo válido")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[\\W_]).{8,}$",
        message = "La contraseña debe tener mínimo 8 caracteres, con mayúscula, minúscula, número y símbolo"
    )
    private String password;

    @NotBlank(message = "Debes repetir la contraseña")
    private String repeatPass;

    @NotBlank(message = "El teléfono móvil es obligatorio")
    @Pattern(regexp = "^[67]\\d{8}$", message = "El teléfono móvil debe tener 9 dígitos y empezar por 6 ó 7")
    private String telefonoMovil;

    @Valid
    @NotNull(message = "La dirección es obligatoria")
    private DireccionOpcionalDTO direccion;
}