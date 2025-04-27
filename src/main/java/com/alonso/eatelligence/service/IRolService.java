package com.alonso.eatelligence.service;

import java.util.Optional;

import com.alonso.eatelligence.model.entity.Rol;
import com.alonso.eatelligence.model.entity.NombreRol;

public interface IRolService {
    Rol save(Rol rol);
    Optional<Rol> findByNombre(NombreRol nombreRol);
    Optional<Rol> findByNombreConOpciones(NombreRol nombreRol);
    Optional<Rol> findByNombreConUsuarios(NombreRol cocinero);
}
