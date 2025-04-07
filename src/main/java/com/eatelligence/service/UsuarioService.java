package com.eatelligence.service;

import com.eatelligence.model.entity.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioService {

    //? READ
    // Método para comprobar la existencia de un usuario por su nombre
    boolean existsUser(String nombre);

    // Método para buscar un usuario por su ID
    Optional<Usuario> searchById(Long id);
    
    // Método para buscar un usuario por su correo electrónico
    Optional<Usuario> searchById(String email);

    // Método para listar todos los usuarios registrados
    List<Usuario> getAll();

    //* CREATE
    Usuario insert(Usuario usuario);

    //+ UPDATE
    void update(Usuario usuario); 


    //! DELETE
    void deleteById(Long id);
}
