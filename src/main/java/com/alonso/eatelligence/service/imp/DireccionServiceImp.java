package com.alonso.eatelligence.service.imp;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alonso.eatelligence.model.entity.Direccion;
import com.alonso.eatelligence.model.entity.Usuario;
import com.alonso.eatelligence.repository.IDireccionRepository;
import com.alonso.eatelligence.service.IDireccionService;

@Service
public class DireccionServiceImp implements IDireccionService {

    @Autowired
    private IDireccionRepository direccionRepository;

    @Override
    public List<Direccion> getDireccionesUsuario(Long usuarioId) {
        return direccionRepository.findByUsuarioId(usuarioId);
    }

    @Override
    public Optional<Direccion> getById(Long id) {
        return direccionRepository.findById(id);
    }

    @Override
    public List<Direccion> findAllByUsuario(Usuario usuario) {
        return this.direccionRepository.findByUsuario(usuario);
    }
}
