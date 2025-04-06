package com.eatelligence.service;

import com.eatelligence.model.entity.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioService {

    //? READ
    // Método para comprobar la existencia de un usuario por su nombre
    boolean existeUsuario(String nombre);

    // Método para buscar un usuario por su ID
    Optional<Usuario> buscarPorId(Long id);
    
    // Método para buscar un usuario por su correo electrónico
    Optional<Usuario> buscarPorEmail(String email);

    // Método para listar todos los usuarios registrados
    List<Usuario> listarTodos();

    //* CREATE
    Usuario insertar(Usuario usuario);

    //+ UPDATE
    void actualizar(Usuario usuario); 


    //! DELETE
    void eliminar(Long id);
}
