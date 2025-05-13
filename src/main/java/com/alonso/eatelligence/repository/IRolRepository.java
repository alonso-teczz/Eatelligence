package com.alonso.eatelligence.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.alonso.eatelligence.model.entity.Rol;
import com.alonso.eatelligence.model.entity.NombreRol;

@Repository
public interface IRolRepository extends JpaRepository<Rol, Long> {
    Optional<Rol> findByNombre(NombreRol nombre);
    @Query("SELECT r FROM Rol r LEFT JOIN FETCH r.opciones WHERE r.nombre = :nombre")
    Optional<Rol> findByNombreConOpciones(NombreRol nombre);

  @Query("""
    SELECT r
    FROM Rol r
    LEFT JOIN FETCH r.usuarioRoles ur
    LEFT JOIN FETCH ur.usuario
    WHERE r.nombre = :nombre
  """)
  Optional<Rol> findByNombreConUsuarios(@Param("nombre") NombreRol nombre);
}
