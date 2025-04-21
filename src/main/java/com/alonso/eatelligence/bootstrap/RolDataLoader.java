package com.alonso.eatelligence.bootstrap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.alonso.eatelligence.model.entity.Rol;
import com.alonso.eatelligence.model.entity.Rol.NombreRol;
import com.alonso.eatelligence.service.IRolService;

@Component
public class RolDataLoader implements CommandLineRunner {

    @Autowired
    private IRolService rolService;

    @Override
    public void run(String... args) {
        for (NombreRol nombreRol : NombreRol.values()) {
            this.rolService.findByNombre(nombreRol)
                .orElseGet(() -> this.rolService.save(Rol.builder()
                    .nombre(nombreRol)
                    .build()
                ));
        }
    }
}