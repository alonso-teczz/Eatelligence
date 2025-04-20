package com.alonso.eatelligence.service.imp;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alonso.eatelligence.model.entity.Rol;
import com.alonso.eatelligence.model.entity.Rol.NombreRol;
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

}
