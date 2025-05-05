package com.alonso.eatelligence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.alonso.eatelligence.model.entity.Plato;
import com.alonso.eatelligence.model.entity.Restaurante;



@Repository
public interface IPlatoRepository extends JpaRepository<Plato, Long> {
    long countByRestaurante(Restaurante restaurante);
}