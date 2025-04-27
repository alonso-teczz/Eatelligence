package com.alonso.eatelligence.bootstrap;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.alonso.eatelligence.model.entity.Direccion;
import com.alonso.eatelligence.model.entity.Restaurante;
import com.alonso.eatelligence.model.entity.NombreRol;
import com.alonso.eatelligence.model.entity.Usuario;
import com.alonso.eatelligence.service.imp.RestauranteServiceImp;
import com.alonso.eatelligence.service.imp.RolServiceImp;
import com.alonso.eatelligence.service.imp.UsuarioServiceImp;

@Component
@DependsOn("rolDataLoader")
@Order(2)
public class UserDataLoader implements CommandLineRunner {

    @Autowired
    private UsuarioServiceImp usuarioService;

    @Autowired
    private RolServiceImp rolService;

    @Autowired
    private RestauranteServiceImp restauranteService;
    
    @Override
    public void run(String... args) {
        if (this.usuarioService.findByUsername("alonso-admin") == null) {
            // Crear usuario y su dirección
            Usuario admin = Usuario.builder()
                .username("alonso-admin")
                .password(this.usuarioService.encodePassword("admin"))
                .email("admin@demo.com")
                .nombre("Alonso")
                .apellidos("Diez Garcia")
                .telefonoMovil("666666666")
                .verificado(true)
                .roles(Set.of(
                    this.rolService.findByNombre(NombreRol.ADMIN).orElseThrow(),
                    this.rolService.findByNombre(NombreRol.CLIENTE).orElseThrow()
                ))
                .build();
    
            Direccion direccionUsuario = Direccion.builder()
                .calle("Calle del Desarrollo")
                .numCalle("33")
                .ciudad("Zaragoza")
                .provincia("Zaragoza")
                .codigoPostal("50001")
                .build();
    
            direccionUsuario.setUsuario(admin);
            admin.getDirecciones().add(direccionUsuario);
    
            // Dirección del restaurante
            Direccion direccionRestaurante = Direccion.builder()
                .calle("Calle de la Tecnología")
                .numCalle("42")
                .ciudad("Madrid")
                .provincia("Madrid")
                .codigoPostal("28001")
                .build();
    
            // Crear restaurante con todo asociado
            Restaurante restaurante = Restaurante.builder()
                .nombreComercial("Restaurante Alonso")
                .emailEmpresa("contacto@restaurantealonso.com")
                .telefonoFijo("911111111")
                .propietario(admin) // usuario aún no guardado
                .verificado(true)
                .direccion(direccionRestaurante)
                .build();
    
            direccionRestaurante.setRestaurante(restaurante);
            // Guardar restaurante (Hibernate persistirá en cascada el usuario y sus direcciones)
            this.restauranteService.save(restaurante);
        }
    
        // Crear usuario cliente normal si no existe
        if (this.usuarioService.findByUsername("cliente-demo") == null) {
            Usuario cliente = Usuario.builder()
                .username("cliente-demo")
                .password(this.usuarioService.encodePassword("cliente"))
                .email("diezgarciaalonso@gmail.com")
                .nombre("Lucía")
                .apellidos("Gómez Pérez")
                .telefonoMovil("644444444")
                .verificado(true)
                .roles(Set.of(
                    this.rolService.findByNombre(NombreRol.CLIENTE).orElseThrow()
                ))
                .build();
    
            Direccion direccionCliente = Direccion.builder()
                .calle("Calle de la Amistad")
                .numCalle("15")
                .ciudad("Barcelona")
                .provincia("Barcelona")
                .codigoPostal("08001")
                .build();
    
            direccionCliente.setUsuario(cliente);
            cliente.getDirecciones().add(direccionCliente);
    
            this.usuarioService.save(cliente);
        }
    }    
}