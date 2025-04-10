package com.alonso.eatelligence.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.alonso.eatelligence.model.dto.ClienteRegistroDTO;
import com.alonso.eatelligence.model.dto.RestauranteRegistroDTO;
import com.alonso.eatelligence.model.embed.Direccion;
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
        return Usuario.builder()
            .nombre(cliente.getNombre())
            .email(cliente.getEmail())
            .telefono(cliente.getTelefono())
            .direccion(
                Direccion.builder()
                .calle(cliente.getCalle())
                .numCalle(cliente.getNumCalle())
                .ciudad(cliente.getCiudad())
                .provincia(cliente.getProvincia())
                .codigoPostal(cliente.getCodigoPostal())
                .latitud(cliente.getLatitud())
                .longitud(cliente.getLongitud())
                .build()
            )
            .contrasena(this.encodePassword(cliente.getContrasena()))
            .build();
    }

    public Restaurante restDTOtoEntity(RestauranteRegistroDTO restaurante) {
        return Restaurante.builder()
            .propietario(this.clientDTOtoEntity(restaurante))
            .nombreComercial(restaurante.getNombreComercial())
            .descripcion(restaurante.getDescripcion())
            .build();
    }    

    @Override
    public Usuario insert(Usuario usuario) {
        return this.usuarioRepository.save(usuario);
    }

    @Override
    public void update(Usuario usuario) {
        this.usuarioRepository.save(usuario);
    }

    @Override
    public Optional<Usuario> searchById(Long id) {
        return this.usuarioRepository.findById(id);
    }

    @Override
    public Optional<Usuario> searchById(String email) {
        return this.usuarioRepository.findByEmail(email);
    }

    @Override
    public List<Usuario> getAll() {
        return this.usuarioRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        usuarioRepository.deleteById(id);
    }

    @Override
    public boolean existsUser(String nombre) {
        return this.usuarioRepository.existsByNombre(nombre);
    }
}
