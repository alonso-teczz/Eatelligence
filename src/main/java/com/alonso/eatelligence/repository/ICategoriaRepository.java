package com.alonso.eatelligence.repository;

import com.alonso.eatelligence.model.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ICategoriaRepository extends JpaRepository<Categoria, Long> {
    Optional<Categoria> findByNombre(Categoria.NombreCategoria nombre);
}