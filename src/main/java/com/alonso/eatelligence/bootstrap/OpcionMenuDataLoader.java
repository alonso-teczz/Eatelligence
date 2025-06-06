package com.alonso.eatelligence.bootstrap;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.alonso.eatelligence.model.entity.OpcionMenu;
import com.alonso.eatelligence.model.entity.Rol;
import com.alonso.eatelligence.model.entity.NombreRol;
import com.alonso.eatelligence.service.IMenuService;
import com.alonso.eatelligence.service.IRolService;

@Component
@DependsOn("rolDataLoader")
@Order(3)
public class OpcionMenuDataLoader implements CommandLineRunner {

    @Autowired
    private IMenuService menuService;

    @Autowired
    private IRolService rolService;

    @Override
    public void run(String... args) {
        cargarOpciones();
    }

    /**
     * Carga las opciones del menú en la base de datos, si no existen.
     * Las opciones se definen en una lista y se recorren para verificar si
     * existen en la base de datos. Si no existen, se crean.
     * Después de cargar las opciones, se asignan a los roles correspondientes.
     */
    private void cargarOpciones() {
        List<OpcionMenu> opciones = List.of(
            // --- Mi Cuenta ---
            // crearOpcion("Ajustes",             "/settings",         "Mi Cuenta",      1),
            crearOpcion("Cerrar sesión",       "/logout",           "Mi Cuenta",      2),
            // crearOpcion("Historial de pedidos","/order-history",        "Mi Cuenta",      3),
            // crearOpcion("Pedidos",             "/orders",          "Mi Cuenta",      4),
            // crearOpcion("Rutas",               "/routes",            "Mi Cuenta",      5),
            crearOpcion("Administración",    "/admin",  "Mi Cuenta", 6),
        
            // --- Administración ---
            crearOpcion("Inicio",    "/admin/dashboard",  "Gestión", 1),
            crearOpcion("Menú",              "/admin/plates",     "Gestión", 2),
            crearOpcion("Horario", "/admin/schedule", "Gestión", 3),
        
            // --- Plantilla (submenú) ---
            crearOpcion("Equipo de cocina",           "/admin/cooks",  "Personal",      1),
            crearOpcion("Equipo de reparto",        "/admin/deliverymen","Personal",     2)
        
            // --- Estadísticas ---
            // crearOpcion("Gráficos",            "/admin/charts",     "Informes",   3),
            // crearOpcion("Registros",              "/admin/tables",     "Informes",   4)
        );        

        for (OpcionMenu opcion : opciones) {
            this.menuService.findByUrl(opcion.getUrl()).ifPresentOrElse(
                existente -> {},
                () -> this.menuService.save(opcion)
            );
        }

        asignarOpciones(NombreRol.ADMIN, "/logout", "/admin", "/admin/dashboard", "/admin/plates", "/admin/cooks", "/admin/deliverymen", "/admin/schedule");
        asignarOpciones(NombreRol.REPARTIDOR, "/logout");
        asignarOpciones(NombreRol.COCINERO, "/logout");
        asignarOpciones(NombreRol.CLIENTE, "/logout");
    }

    /**
     * Crea una opción de menú con los datos dados.
     *
     * @param nombre nombre de la opción de menú
     * @param url    ruta correspondiente a la opción
     * @param seccion sección donde se encuentra la opción
     * @param orden   orden de aparición en la sección
     * @return una opción de menú con los datos dados
     */
    private OpcionMenu crearOpcion(String nombre, String url, String seccion, int orden) {
        return OpcionMenu.builder()
            .nombre(nombre)
            .url(url)
            .seccion(seccion)
            .orden(orden)
            .activo(true)
            .build();
    }

    /**
     * Asigna las opciones de menú correspondientes a un rol.
     * 
     * @param rolNombre nombre del rol al que se asignarán las opciones
     * @param rutas     rutas de las opciones de menú a asignar
     */
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