package com.alonso.eatelligence.model.vo;

import java.time.LocalDateTime;
import java.util.List;

import com.alonso.eatelligence.model.embed.Direccion;
import com.alonso.eatelligence.model.entity.Pedido;
import com.alonso.eatelligence.model.entity.Rol;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioVO {

    private Long id;

    private String nombre;

    private String email;

    private String telefono;

    private Direccion direccion;

    private String contrasena;

    private Rol rol;

    private boolean activo;

    private LocalDateTime fechaRegistro;

    private List<Pedido> pedidos;
}
