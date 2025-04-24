package com.alonso.eatelligence.repository;

import com.alonso.eatelligence.model.entity.Plato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IPlatoRepository extends JpaRepository<Plato, Long> {

}