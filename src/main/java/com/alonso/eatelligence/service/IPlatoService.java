package com.alonso.eatelligence.service;

import java.util.List;
import java.util.Optional;

import com.alonso.eatelligence.model.dto.AltaPlatoDTO;
import com.alonso.eatelligence.model.entity.Plato;
import com.alonso.eatelligence.model.entity.Restaurante;

public interface IPlatoService {
    List<Plato> getAll();
    Optional<Plato> findById(Long id);
    Plato saveFromDTO(AltaPlatoDTO dto, Restaurante restaurante);
    Plato updateFromDTO(Long id, AltaPlatoDTO dto);
    void delete(Long id);
    long countAll();
    long countByRestaurante(Restaurante restaurante);
    void save(Plato p);
}