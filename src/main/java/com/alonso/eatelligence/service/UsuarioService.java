package com.alonso.eatelligence.service;

import java.util.List;
import java.util.Optional;

import com.alonso.eatelligence.model.entity.Usuario;

public interface UsuarioService {

    //? READ
    // Método para comprobar la existencia de un usuario por su nombre
    boolean existsByNombre(String nombre);

    // Método para buscar un usuario por su ID
    Optional<Usuario> findById(Long id);

    // Método para listar todos los usuarios registrados
    List<Usuario> getAllUsers();

    //* CREATE
    Usuario save(Usuario usuario);

    //+ UPDATE
    void update(Usuario usuario); 

    //! DELETE
    void deleteById(Long id);
}
