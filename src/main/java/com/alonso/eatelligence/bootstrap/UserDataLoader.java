package com.alonso.eatelligence.bootstrap;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.alonso.eatelligence.model.entity.Direccion;
import com.alonso.eatelligence.model.entity.NombreRol;
import com.alonso.eatelligence.model.entity.Restaurante;
import com.alonso.eatelligence.model.entity.Usuario;
import com.alonso.eatelligence.model.entity.UsuarioRol;
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
        if (this.usuarioService.findByUsername("alonso-admin").isEmpty()) {
            // Crear usuario y su dirección
            Usuario admin = Usuario.builder()
                .username("alonso-admin")
                .password(this.usuarioService.encodePassword("admin"))
                .email("admin@demo.com")
                .nombre("Alonso")
                .apellidos("Diez Garcia")
                .telefonoMovil("666666666")
                .verificado(true)
                .build();

            admin.setRoles(List.of(
                UsuarioRol.builder().usuario(admin).rol(this.rolService.findByNombre(NombreRol.ADMIN).orElseThrow()).build(),
                UsuarioRol.builder().usuario(admin).rol(this.rolService.findByNombre(NombreRol.CLIENTE).orElseThrow()).build()
            ));

            Direccion direccionUsuario = Direccion.builder()
                .calle("Calle San Blas")
                .numCalle("2")
                .ciudad("Zamora")
                .provincia("Zamora")
                .codigoPostal("49023")
                .latitud(41.51305690290546)
                .longitud(-5.7484801677823185)
                .build();
    
            direccionUsuario.setUsuario(admin);
            admin.getDirecciones().add(direccionUsuario);
    
            // Dirección del restaurante
            Direccion direccionRestaurante = Direccion.builder()
                .calle("Calle de Magallanes")
                .numCalle("22")
                .ciudad("Zamora")
                .provincia("Zamora")
                .codigoPostal("49020")
                .latitud(41.505774500932446)
                .longitud(-5.733302320887748)
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
        if (this.usuarioService.findByUsername("cliente-demo").isEmpty()) {
            Usuario cliente = Usuario.builder()
                .username("cliente-demo")
                .password(this.usuarioService.encodePassword("cliente"))
                .email("diezgarciaalonso@gmail.com")
                .nombre("Lucía")
                .apellidos("Gómez Pérez")
                .telefonoMovil("644444444")
                .verificado(true)
                .build();
            
            cliente.setRoles(List.of(
                UsuarioRol.builder().usuario(cliente).rol(this.rolService.findByNombre(NombreRol.CLIENTE).orElseThrow()).build()
            ));

            Direccion direccionCliente = Direccion.builder()
                .calle("Calle de Arcenillas")
                .numCalle("5")
                .ciudad("Zamora")
                .provincia("Zamora")
                .codigoPostal("49028")
                .latitud(41.49769570030578)
                .longitud(-5.738943647973565)
                .build();
    
            direccionCliente.setUsuario(cliente);
            cliente.getDirecciones().add(direccionCliente);
    
            this.usuarioService.save(cliente);
        }
    }    
}