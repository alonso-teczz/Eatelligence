package com.alonso.eatelligence.service;

import java.util.Optional;

import com.alonso.eatelligence.model.entity.Alergeno;

public interface IAlergenoService {
    Alergeno save(Alergeno alergeno);
    Optional<Alergeno> findByNombre(Alergeno.NombreAlergeno nombre);
}
