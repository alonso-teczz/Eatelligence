package com.alonso.eatelligence.service;

import java.util.List;
import java.util.Optional;

import com.alonso.eatelligence.model.entity.Categoria;

public interface ICategoriaService {
    Optional<Categoria> findByNombre(Categoria.NombreCategoria nombre);
    List<Categoria> getAll();
    Categoria save(Categoria categoria);
}
