package com.alonso.eatelligence.service;

import com.alonso.eatelligence.model.dto.PlatoDTO;
import com.alonso.eatelligence.model.entity.Plato;
import com.alonso.eatelligence.model.entity.Restaurante;

import java.util.List;

public interface IPlatoService {
    List<Plato> getAll();
    Plato findyById(Long id);
    Plato saveFromDTO(PlatoDTO dto, Restaurante restaurante);
    Plato updateFromDTO(Long id, PlatoDTO dto);
    void delete(Long id);
    long countAll();
}