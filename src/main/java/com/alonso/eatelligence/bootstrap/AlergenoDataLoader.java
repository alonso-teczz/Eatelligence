package com.alonso.eatelligence.bootstrap;

import org.springframework.core.annotation.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.alonso.eatelligence.model.entity.Alergeno;
import com.alonso.eatelligence.model.entity.Alergeno.NombreAlergeno;
import com.alonso.eatelligence.service.imp.AlergenoServiceImp;

import lombok.extern.slf4j.Slf4j;

@Component
@Order(5)
@Transactional
@Slf4j
public class AlergenoDataLoader implements CommandLineRunner {

    @Autowired
    private AlergenoServiceImp alergenoService;

    @Override
    public void run(String... args) {
        log.info(">>> Arranca AlergenoDataLoader");
        for (NombreAlergeno na : NombreAlergeno.values()) {
            alergenoService.findByNombre(na)
                    .orElseGet(() -> {
                        log.info("   → Creando alérgeno {}", na);
                        return alergenoService.save(
                                Alergeno.builder().nombre(na).build());
                    });
        }
    }
}
