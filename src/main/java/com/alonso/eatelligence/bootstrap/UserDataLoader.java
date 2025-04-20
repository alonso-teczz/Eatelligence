package com.alonso.eatelligence.bootstrap;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.alonso.eatelligence.model.entity.Rol.NombreRol;
import com.alonso.eatelligence.model.entity.Usuario;
import com.alonso.eatelligence.service.imp.RolServiceImp;
import com.alonso.eatelligence.service.imp.UsuarioServiceImp;

//! CLASE CREADA CON EL FIN DE EVITAR LA CREACIÓN DE UN USUARIO ADMINISTRADOR EN CADA INICIO DE APLICACIÓN
@Component
public class UserDataLoader implements CommandLineRunner {

    @Autowired
    private UsuarioServiceImp usuarioService;

    @Autowired
    private RolServiceImp rolService;
    
    @Override
    public void run(String... args) {
        if (this.usuarioService.findByUsername("alonso-admin") != null) {
            return;
        }
        
        this.usuarioService.save(Usuario.builder()
            .username("alonso-admin")
            .password(this.usuarioService.encodePassword("admin"))
            .email("diezgarciaalonso@gmail.com")
            .nombre("Alonso")
            .apellidos("Diez Garcia")
            .telefonoMovil("666666666")
            .verificado(true)
            .roles(List.of(
                this.rolService.findByNombre(NombreRol.ADMIN).orElseThrow(() -> new RuntimeException("Error al insertar el rol")),
                this.rolService.findByNombre(NombreRol.CLIENTE).orElseThrow(() -> new RuntimeException("Error al insertar el rol"))
            ))
            .build()
        );
    }
    
}
