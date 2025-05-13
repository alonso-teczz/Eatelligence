package com.alonso.eatelligence.service;

import java.util.List;
import java.util.Optional;

import com.alonso.eatelligence.model.entity.NombreRol;
import com.alonso.eatelligence.model.entity.Restaurante;
import com.alonso.eatelligence.model.entity.Usuario;

public interface IUsuarioService {

    //? READ
    Optional<Usuario> findByUsername(String username);
    Optional<Usuario> findById(Long id);
    Optional<Usuario> findByUsernameAndEmail(String username, String email);    
    List<Usuario> getAllUsers();
    List<Usuario> findAllByRestauranteAsignadoAndRol(Restaurante restaurante, NombreRol rol);
    long countByRestauranteAsignadoAndRol(Restaurante restaurante, NombreRol rol);
    boolean existsByUsername(String username);
    boolean checkPassword(Usuario u, String password);
    
    //# CREATE
    Usuario save(Usuario usuario);
    
    //+ UPDATE
    void update(Usuario usuario); 
    void addRoleToUser(String username, NombreRol rol);
    void asignarRestaurante(Usuario usuario, Restaurante r);
    
    //! DELETE
    void deleteById(Long id);
    
}
