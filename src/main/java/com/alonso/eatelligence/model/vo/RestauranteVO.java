package com.alonso.eatelligence.model.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class RestauranteVO extends UsuarioVO {

    private UsuarioVO propietario;

    private String nombreComercial;

    private String descripcion;

    private String telefonoFijo;
}
