package com.alonso.eatelligence.service;

import java.util.List;
import java.util.Optional;

import com.alonso.eatelligence.model.entity.Restaurante;

public interface IRestauranteService {
    Optional<Restaurante> findById(Long id);

    List<Restaurante> getAllRestaurants();

    Restaurante save(Restaurante restaurante);

    void update(Restaurante restaurante);
    
    void deleteById(Long id);
}
