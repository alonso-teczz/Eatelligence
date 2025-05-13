package com.alonso.eatelligence.service;

import com.alonso.eatelligence.model.entity.Direccion;
import com.alonso.eatelligence.model.entity.Usuario;

import java.util.List;
import java.util.Optional;

public interface IDireccionService {
    List<Direccion> getDireccionesUsuario(Long usuarioId);
    Optional<Direccion> getById(Long id);
    List<Direccion> findAllByUsuario(Usuario usuario);
}
