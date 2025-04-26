package com.alonso.eatelligence.model.dto;

import com.alonso.eatelligence.model.entity.Rol.NombreRol;

import jakarta.validation.constraints.*;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmpleadoDTO {

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(max = 50, message = "El nombre de usuario no puede superar los 50 caracteres")
    private String username;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Debes introducir un correo válido")
    private String email;

    @NotNull(message = "Debes seleccionar un rol")
    private NombreRol rol;
}
