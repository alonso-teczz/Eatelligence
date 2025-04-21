package com.alonso.eatelligence.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.alonso.eatelligence.model.entity.Rol;
import com.alonso.eatelligence.model.entity.Rol.NombreRol;

@Repository
public interface IRolRepository extends JpaRepository<Rol, Long> {
    Optional<Rol> findByNombre(NombreRol nombre);
    @Query("SELECT r FROM Rol r LEFT JOIN FETCH r.opciones WHERE r.nombre = :nombre")
    Optional<Rol> findByNombreConOpciones(Rol.NombreRol nombre);
}
