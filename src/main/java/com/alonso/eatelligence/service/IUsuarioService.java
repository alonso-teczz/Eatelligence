package com.alonso.eatelligence.service;

import java.util.List;
import java.util.Optional;

import com.alonso.eatelligence.model.entity.Rol.NombreRol;
import com.alonso.eatelligence.model.entity.Usuario;

public interface IUsuarioService {

    // Método para comprobar la existencia de un usuario por su nombre
    boolean existsByUsername(String username);

    boolean checkPassword(Usuario u, String password);
    
    //? READ
    // Método para buscar un usuario por su ID
    Optional<Usuario> findById(Long id);

    Usuario findByUsername(String username);

    Optional<Usuario> findByUsernameAndEmail(String username, String email);

    // Método para listar todos los usuarios registrados
    List<Usuario> getAllUsers();

    //# CREATE
    Usuario save(Usuario usuario);

    //+ UPDATE
    void update(Usuario usuario); 

    //! DELETE
    void deleteById(Long id);

    void addRoleToUser(String username, NombreRol rol);

}
