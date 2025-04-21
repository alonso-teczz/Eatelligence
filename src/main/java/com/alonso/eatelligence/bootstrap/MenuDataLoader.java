package com.alonso.eatelligence.bootstrap;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alonso.eatelligence.model.entity.OpcionMenu;
import com.alonso.eatelligence.model.entity.Rol;
import com.alonso.eatelligence.model.entity.Rol.NombreRol;
import com.alonso.eatelligence.service.IMenuService;
import com.alonso.eatelligence.service.IRolService;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MenuDataLoader {

    @Autowired
    private IMenuService menuService;

    @Autowired
    private IRolService rolService;

    @PostConstruct
    public void cargarOpciones() {
        List<OpcionMenu> opciones = List.of(
            crearOpcion("Ajustes", "/settings", "Mi Cuenta", 1),
            crearOpcion("Cerrar sesión", "/logout", "Mi Cuenta", 2),
            crearOpcion("Historial de pedidos", "/historial", "Mi Cuenta", 3),
            crearOpcion("Panel de control", "/admin", "Mi Cuenta", 4),
            crearOpcion("Rutas", "/rutas", "Mi Cuenta", 5),
            crearOpcion("Pedidos", "/pedidos", "Mi Cuenta", 6)
        );

        for (OpcionMenu opcion : opciones) {
            this.menuService.findByUrl(opcion.getUrl()).ifPresentOrElse(
                existente -> {},
                () -> this.menuService.save(opcion)
            );
        }

        asignarOpciones(NombreRol.ADMIN, "/settings", "/logout", "/historial", "/admin");
        asignarOpciones(NombreRol.REPARTIDOR, "/settings", "/logout", "/historial", "/rutas");
        asignarOpciones(NombreRol.COCINERO, "/settings", "/logout", "/historial", "/pedidos");
        asignarOpciones(NombreRol.CLIENTE, "/settings", "/logout", "/historial");
    }

    private OpcionMenu crearOpcion(String nombre, String url, String seccion, int orden) {
        return OpcionMenu.builder()
            .nombre(nombre)
            .url(url)
            .seccion(seccion)
            .orden(orden)
            .activo(true)
            .build();
    }

    private void asignarOpciones(NombreRol rolNombre, String... rutas) {
        Rol rol = this.rolService.findByNombreConOpciones(rolNombre)
            .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + rolNombre));

        for (String ruta : rutas) {
            this.menuService.findByUrl(ruta).ifPresent(opcion -> {
                if (!rol.getOpciones().contains(opcion)) {
                    rol.getOpciones().add(opcion);
                }
            });
        }

        this.rolService.save(rol);
    }
}