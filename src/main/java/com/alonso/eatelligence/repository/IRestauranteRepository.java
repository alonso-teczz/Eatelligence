package com.alonso.eatelligence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alonso.eatelligence.model.entity.Restaurante;

public interface IRestauranteRepository extends JpaRepository<Restaurante, Long> {
    
}
