package com.alonso.eatelligence.model.dto;

import com.alonso.eatelligence.validation.annotations.DireccionCompleta;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DireccionCompleta
public class DireccionOpcionalDTO {
    private String calle;
    private String numCalle;
    private String ciudad;
    private String provincia;
    private String codigoPostal;
    private Double latitud;
    private Double longitud;
}