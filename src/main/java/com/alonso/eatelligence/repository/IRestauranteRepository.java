package com.alonso.eatelligence.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alonso.eatelligence.model.entity.Restaurante;
import com.alonso.eatelligence.model.entity.Usuario;

public interface IRestauranteRepository extends JpaRepository<Restaurante, Long> {

    Optional<Restaurante> findByPropietario(Usuario propietario);
    
}
