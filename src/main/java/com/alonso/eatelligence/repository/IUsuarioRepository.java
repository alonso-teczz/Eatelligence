package com.alonso.eatelligence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.alonso.eatelligence.model.entity.NombreRol;
import com.alonso.eatelligence.model.entity.Restaurante;
import com.alonso.eatelligence.model.entity.Usuario;

@Repository
public interface IUsuarioRepository extends JpaRepository<Usuario, Long> {
  boolean existsByUsername(String username);
  Optional<Usuario> findByUsername(String username);
  Optional<Usuario> findByUsernameAndEmail(String username, String email);
  @Query("""
    SELECT COUNT(u) 
    FROM Usuario u 
    JOIN u.roles r 
    WHERE u.restauranteAsignado = :restaurante 
    AND r.nombre = :rol
  """)
  long countByRestauranteAsignadoAndRol(
    @Param("restaurante") Restaurante restaurante,
    @Param("rol") NombreRol rol
  );

  List<Usuario> findAllByRestauranteAsignadoAndRolesNombre(Restaurante restauranteAsignado, NombreRol rol);
}
