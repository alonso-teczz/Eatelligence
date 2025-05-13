package com.alonso.eatelligence.bootstrap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.alonso.eatelligence.model.entity.Categoria;
import com.alonso.eatelligence.model.entity.Categoria.NombreCategoria;
import com.alonso.eatelligence.service.ICategoriaService;

@Component
@Order(4)
public class CategoriaDataLoader implements CommandLineRunner {

    @Autowired
    private ICategoriaService categoriaService;

    @Override
    public void run(String... args) {
        for (NombreCategoria nombre : NombreCategoria.values()) {
            this.categoriaService.findByNombre(nombre)
                .orElseGet(() -> 
                    this.categoriaService.save(Categoria.builder()
                    .nombre(nombre)
                    .build()
                ));
        }
    }
}
