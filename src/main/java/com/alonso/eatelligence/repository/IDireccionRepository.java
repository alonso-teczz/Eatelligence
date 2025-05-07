package com.alonso.eatelligence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.alonso.eatelligence.model.entity.Direccion;
import com.alonso.eatelligence.model.entity.Usuario;

@Repository
public interface IDireccionRepository extends JpaRepository<Direccion, Long> {
    List<Direccion> findByUsuarioId(Long usuarioId);
    List<Direccion> findByUsuario(Usuario usuario);
}
