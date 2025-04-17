package com.alonso.eatelligence.model.dto;

import com.alonso.eatelligence.validation.annotations.ExistsUser;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginDTO {

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @ExistsUser(message = "Usuario no encontrado")
    private String username;

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
}
