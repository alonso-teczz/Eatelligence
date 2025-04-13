package com.alonso.eatelligence.model.vo;

import java.time.LocalDateTime;
import java.util.List;

import com.alonso.eatelligence.model.entity.Pedido;
import com.alonso.eatelligence.model.entity.Rol;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UsuarioVO {

    private Long id;

    private String username;

    private String nombre;

    private String apellidos;

    private String email;

    private String telefonoMovil;

    private String contrasena;

    private boolean activo;
    
    private List<DireccionVO> direcciones;

    private Rol rol;

    private LocalDateTime fechaRegistro;

    private List<Pedido> pedidos;
}
