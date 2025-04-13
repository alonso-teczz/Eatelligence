package com.alonso.eatelligence.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.alonso.eatelligence.model.dto.ClienteRegistroDTO;
import com.alonso.eatelligence.model.dto.RestauranteRegistroDTO;
import com.alonso.eatelligence.model.entity.Direccion;
import com.alonso.eatelligence.model.entity.Restaurante;
import com.alonso.eatelligence.model.entity.Usuario;
import com.alonso.eatelligence.repository.UsuarioRepository;
import com.alonso.eatelligence.service.UsuarioService;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public String encodePassword(String password) {
        return this.passwordEncoder.encode(password);
    }

    public boolean matchesPassword(String rawPassword, String hashedPassword) {
        return this.passwordEncoder.matches(rawPassword, hashedPassword);
    }

    public Usuario clientDTOtoEntity(ClienteRegistroDTO cliente) {
        Usuario usuario = Usuario.builder()
            .username(cliente.getUsername())
            .nombre(cliente.getNombre())
            .apellidos(cliente.getApellidos())
            .email(cliente.getEmail())
            .telefonoMovil(cliente.getTelefonoMovil())
            .password(this.encodePassword(cliente.getPassword()))
            .build();

        if (cliente.getDireccion() != null) {
            usuario.getDirecciones().add(
                Direccion.builder()
                .calle(cliente.getDireccion().getCalle())
                .numCalle(cliente.getDireccion().getNumCalle())
                .ciudad(cliente.getDireccion().getCiudad())
                .provincia(cliente.getDireccion().getProvincia())
                .codigoPostal(cliente.getDireccion().getCodigoPostal())
                .latitud(cliente.getDireccion().getLatitud())
                .longitud(cliente.getDireccion().getLongitud())
                .usuario(usuario)
                .build()
            );
        }

        return usuario;
    }


    public Restaurante restDTOtoEntity(RestauranteRegistroDTO restaurante) {
        Restaurante restauranteEntity = Restaurante.builder()
            .propietario(this.clientDTOtoEntity(restaurante.getPropietario()))
            .nombreComercial(restaurante.getNombreComercial())
            .descripcion(restaurante.getDescripcion())
            .telefonoFijo(restaurante.getTelefonoFijo())
            .direccion(
                Direccion.builder()
                .calle(restaurante.getDireccionRestaurante().getCalle())
                .numCalle(restaurante.getDireccionRestaurante().getNumCalle())
                .ciudad(restaurante.getDireccionRestaurante().getCiudad())
                .provincia(restaurante.getDireccionRestaurante().getProvincia())
                .codigoPostal(restaurante.getDireccionRestaurante().getCodigoPostal())
                .latitud(restaurante.getDireccionRestaurante().getLatitud())
                .longitud(restaurante.getDireccionRestaurante().getLongitud())
                .build()
            )
            .build();

        restauranteEntity.getDireccion().setRestaurante(restauranteEntity);

        return restauranteEntity;
    }
      

    @Override
    public Usuario save(Usuario usuario) {
        return this.usuarioRepository.save(usuario);
    }

    @Override
    public void update(Usuario usuario) {
        this.usuarioRepository.save(usuario);
    }

    @Override
    public Optional<Usuario> findById(Long id) {
        return this.usuarioRepository.findById(id);
    }


    @Override
    public List<Usuario> getAllUsers() {
        return this.usuarioRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        usuarioRepository.deleteById(id);
    }

    @Override
    public boolean existsByUsername(String nombre) {
        return this.usuarioRepository.existsByUsername(nombre);
    }

}
