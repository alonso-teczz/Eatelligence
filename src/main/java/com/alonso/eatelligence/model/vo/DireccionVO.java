package com.alonso.eatelligence.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class DireccionVO {

    private String calle;

    private String numCalle;

    private String ciudad;

    private String provincia;
    
    private String codigoPostal;
}
