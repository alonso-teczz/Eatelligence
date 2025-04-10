package com.alonso.eatelligence.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.alonso.eatelligence.model.dto.ClienteRegistroDTO;
import com.alonso.eatelligence.model.embed.Direccion;
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

    public Usuario ClientDTOtoEntity(ClienteRegistroDTO usuario) {
        return Usuario.builder()
            .nombre(usuario.getNombre())
            .email(usuario.getEmail())
            .telefono(usuario.getTelefono())
            .direccion(
                Direccion.builder()
                .calle(usuario.getCalle())
                .numCalle(usuario.getNumCalle())
                .ciudad(usuario.getCiudad())
                .provincia(usuario.getProvincia())
                .codigoPostal(usuario.getCodigoPostal())
                .build()
            )
            .contrasena(this.encodePassword(usuario.getContrasena()))
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
