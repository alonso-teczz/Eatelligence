package com.alonso.eatelligence.bootstrap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.alonso.eatelligence.model.entity.Rol;
import com.alonso.eatelligence.model.entity.Rol.NombreRol;
import com.alonso.eatelligence.service.imp.RolServiceImp;

@Component
public class RolDataLoader implements CommandLineRunner {

    @Autowired
    private RolServiceImp rolService;

    public RolDataLoader(RolServiceImp rolRepository) {
        this.rolService = rolRepository;
    }

    @Override
    public void run(String... args) {
        for (NombreRol nombreRol : NombreRol.values()) {
            rolService.findByNombre(nombreRol)
                .orElseGet(() -> rolService.save(Rol.builder()
                    .nombre(nombreRol)
                    .build()
                ));
        }
    }
}