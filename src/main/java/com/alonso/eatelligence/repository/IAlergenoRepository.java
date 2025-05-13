package com.alonso.eatelligence.repository;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.alonso.eatelligence.model.entity.Alergeno;
import com.alonso.eatelligence.model.entity.Alergeno.NombreAlergeno;

@Repository
public interface IAlergenoRepository extends JpaRepository<Alergeno, Long> {
    Optional<Alergeno> findByNombre(NombreAlergeno nombre);
    Set<Alergeno> findByIdIn(Set<Long> ids);
}
