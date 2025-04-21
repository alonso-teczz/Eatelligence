package com.alonso.eatelligence.bootstrap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.alonso.eatelligence.model.entity.Alergeno;
import com.alonso.eatelligence.model.entity.Alergeno.NombreAlergeno;
import com.alonso.eatelligence.service.IAlergenoService;

@Component
public class AlergenoDataLoader implements CommandLineRunner {

    @Autowired
    private IAlergenoService alergenoService;

    @Override
    public void run(String... args) {
        for (NombreAlergeno nombre : NombreAlergeno.values()) {
            this.alergenoService.findByNombre(nombre)
                .orElseGet(() ->
                    this.alergenoService.save(Alergeno.builder()
                    .nombre(nombre)
                    .build()
                ));
        }
    }
}
