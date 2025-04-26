package com.alonso.eatelligence.service;

import java.util.Optional;

import com.alonso.eatelligence.model.entity.Rol;
import com.alonso.eatelligence.model.entity.Rol.NombreRol;

public interface IRolService {
    Optional<Rol> findByNombre(Rol.NombreRol nombreRol);
    Optional<Rol> findByNombreConOpciones(Rol.NombreRol nombreRol);
    Rol save(Rol rol);
    Optional<Rol> findByNombreConUsuarios(NombreRol cocinero);
}
