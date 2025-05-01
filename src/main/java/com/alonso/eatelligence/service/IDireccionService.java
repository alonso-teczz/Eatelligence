package com.alonso.eatelligence.service;

import com.alonso.eatelligence.model.entity.Direccion;

import java.util.List;
import java.util.Optional;

public interface IDireccionService {
    List<Direccion> getDireccionesUsuario(Long usuarioId);
    Optional<Direccion> getById(Long id);
}
