package com.eatelligence.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eatelligence.model.entity.Usuario;
import com.eatelligence.repository.UsuarioRepository;
import com.eatelligence.service.UsuarioService;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public Usuario insertar(Usuario usuario) {
        return this.usuarioRepository.save(usuario);
    }

    @Override
    public void actualizar(Usuario usuario) {
        this.usuarioRepository.save(usuario);
    }

    @Override
    public Optional<Usuario> buscarPorId(Long id) {
        return this.usuarioRepository.findById(id);
    }

    @Override
    public Optional<Usuario> buscarPorEmail(String email) {
        return this.usuarioRepository.findByEmail(email);
    }

    @Override
    public List<Usuario> listarTodos() {
        return this.usuarioRepository.findAll();
    }

    @Override
    public void eliminar(Long id) {
        usuarioRepository.deleteById(id);
    }

    @Override
    public boolean existeUsuario(String nombre) {
        return this.usuarioRepository.existsByNombre(nombre);
    }
}
