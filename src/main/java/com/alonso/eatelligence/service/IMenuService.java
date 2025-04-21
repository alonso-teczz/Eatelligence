package com.alonso.eatelligence.service;

import com.alonso.eatelligence.model.entity.OpcionMenu;

import java.util.List;
import java.util.Optional;

public interface IMenuService {
    List<OpcionMenu> obtenerOpcionesParaUsuario(String username);
    Optional<OpcionMenu> findByUrl(String ruta);
    Object save(OpcionMenu opcion);
}
