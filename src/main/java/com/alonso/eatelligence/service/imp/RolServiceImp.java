package com.alonso.eatelligence.service.imp;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alonso.eatelligence.model.entity.Rol;
import com.alonso.eatelligence.model.entity.NombreRol;
import com.alonso.eatelligence.repository.IRolRepository;
import com.alonso.eatelligence.service.IRolService;

@Service
public class RolServiceImp implements IRolService {

    @Autowired
    private IRolRepository rolRepository;

    @Override
    public Optional<Rol> findByNombre(NombreRol nombreRol) {
        return this.rolRepository.findByNombre(nombreRol);
    }

    public Rol save(Rol rol) {
        return this.rolRepository.save(rol);
    }

    @Override
    public Optional<Rol> findByNombreConOpciones(NombreRol nombreRol) {
        return this.rolRepository.findByNombreConOpciones(nombreRol);
    }

    @Override
    public Optional<Rol> findByNombreConUsuarios(NombreRol cocinero) {
        return this.rolRepository.findByNombreConUsuarios(cocinero);
    }

}
