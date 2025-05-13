package com.alonso.eatelligence.bootstrap;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.alonso.eatelligence.model.entity.Alergeno.NombreAlergeno;
import com.alonso.eatelligence.model.entity.Categoria.NombreCategoria;
import com.alonso.eatelligence.model.entity.Direccion;
import com.alonso.eatelligence.model.entity.Horario;
import com.alonso.eatelligence.model.entity.Plato;
import com.alonso.eatelligence.model.entity.Restaurante;
import com.alonso.eatelligence.model.entity.Usuario;
import com.alonso.eatelligence.model.entity.NombreRol;
import com.alonso.eatelligence.service.imp.AlergenoServiceImp;
import com.alonso.eatelligence.service.imp.CategoriaServiceImp;
import com.alonso.eatelligence.service.imp.RestauranteServiceImp;
import com.alonso.eatelligence.service.imp.RolServiceImp;
import com.alonso.eatelligence.service.imp.UsuarioServiceImp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Order(6)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RestauranteDataLoader implements CommandLineRunner {

    private final UsuarioServiceImp     usuarioService;
    private final RolServiceImp         rolService;
    private final RestauranteServiceImp restauranteService;
    private final CategoriaServiceImp   categoriaService;
    private final AlergenoServiceImp    alergenoService;

    private static final String DEFAULT_PASSWORD = "admin123";
    private static final String TEL_PLACEHOLDER  = "600000000";

    @Override
    @Transactional
    public void run(String... args) {
        log.info(">> Precargando restaurantes de ejemplo");

        // 1-5: Restaurantes existentes
        crearRestaurante(
            "Zamorano Grill House", "grill-admin",
            41.520680, -5.764890,
            List.of(NombreCategoria.TRADICIONAL, NombreCategoria.BRASERIA),
            Set.of(NombreRol.ADMIN, NombreRol.CLIENTE),
            List.of(
                plato("Chuletón de ternera", 22.50, "300 g de carne a la brasa",
                      List.of(NombreAlergeno.GLUTEN)),
                plato("Morcilla zamorana", 8.90, "Morcilla y pan tostado",
                      List.of(NombreAlergeno.GLUTEN)),
                plato("Ensalada parrillera", 9.50, "Verduras y chimichurri",
                      List.of())
            )
        );

        crearRestaurante(
            "La Trattoria del Duero", "trattoria-admin",
            41.492340, -5.740120,
            List.of(NombreCategoria.ITALIANO, NombreCategoria.MEDITERRANEO),
            Set.of(NombreRol.CLIENTE),
            List.of(
                plato("Tagliatelle al pesto",   11.80, "Pasta fresca con pesto",     List.of(NombreAlergeno.GLUTEN, NombreAlergeno.FRUTOS_SECOS)),
                plato("Pizza quattro formaggi", 13.20, "Cuatro quesos artesanos",    List.of(NombreAlergeno.GLUTEN, NombreAlergeno.LACTEOS)),
                plato("Tiramisú clásico",       6.50,  "Postre italiano",            List.of(NombreAlergeno.LACTEOS, NombreAlergeno.HUEVOS))
            )
        );

        crearRestaurante(
            "Sushi Río Negro", "sushi-admin",
            41.535010, -5.772310,
            List.of(NombreCategoria.JAPONES, NombreCategoria.FUSION),
            Set.of(NombreRol.CLIENTE),
            List.of(
                plato("Uramaki de salmón",      12.00, "8 piezas con aguacate",      List.of(NombreAlergeno.PESCADO, NombreAlergeno.SOJA)),
                plato("Sashimi variado",        15.50, "15 cortes frescos",          List.of(NombreAlergeno.PESCADO)),
                plato("Mochi de té verde",      4.20,  "Postre de arroz glutinoso",  List.of())
            )
        );

        crearRestaurante(
            "Tacos Ruta de la Plata", "tacos-admin",
            41.502220, -5.724560,
            List.of(NombreCategoria.MEXICANO, NombreCategoria.COMIDA_RAPIDA),
            Set.of(NombreRol.CLIENTE),
            List.of(
                plato("Taco al pastor",         2.80,  "Cerdo adobado y piña",       List.of(NombreAlergeno.GLUTEN)),
                plato("Burrito de ternera",     7.50,  "Con frijoles y queso",       List.of(NombreAlergeno.LACTEOS, NombreAlergeno.GLUTEN)),
                plato("Nachos con guacamole",   5.90,  "Totopos y guacamole",        List.of())
            )
        );

        crearRestaurante(
            "Green Bowl Zamora", "green-admin",
            41.477890, -5.757430,
            List.of(NombreCategoria.VEGANO, NombreCategoria.VEGETARIANO),
            Set.of(NombreRol.CLIENTE),
            List.of(
                plato("Buddha bowl de quinoa",  9.80, "Quinoa, hummus y verduras",  List.of(NombreAlergeno.FRUTOS_SECOS)),
                plato("Hamburguesa veggie",      8.70, "Heura y pan integral",       List.of(NombreAlergeno.GLUTEN, NombreAlergeno.SOJA)),
                plato("Cheesecake vegano",       5.60, "Queso de anacardos",         List.of(NombreAlergeno.FRUTOS_SECOS))
            )
        );

        // 6-10: Nuevos restaurantes
        crearRestaurante(
            "China Express Zamora", "china-admin",
            41.510000, -5.765000,
            List.of(NombreCategoria.FUSION, NombreCategoria.COMIDA_RAPIDA),
            Set.of(NombreRol.CLIENTE),
            List.of(
                plato("Pollo Kung Pao",       10.50, "Pollo con cacahuetes y especias", List.of(NombreAlergeno.FRUTOS_SECOS, NombreAlergeno.SOJA)),
                plato("Arroz tres delicias",    7.00, "Arroz con vegetales y huevo",      List.of(NombreAlergeno.HUEVOS)),
                plato("Rollitos de primavera",  6.50, "Rollitos fritos",                 List.of(NombreAlergeno.GLUTEN))
            )
        );

        crearRestaurante(
            "Bombay Spice", "india-admin",
            41.517000, -5.750000,
            List.of(NombreCategoria.FUSION, NombreCategoria.COMIDA_RAPIDA),
            Set.of(NombreRol.CLIENTE),
            List.of(
                plato("Pollo Tikka Masala", 12.00, "Pollo en salsa especiada",       List.of(NombreAlergeno.LACTEOS)),
                plato("Samosas vegetarianas",5.50, "Relleno de patata y guisantes", List.of()),
                plato("Arroz basmati",        4.00, "Arroz aromático al vapor",      List.of())
            )
        );

        crearRestaurante(
            "BBQ Smokehouse", "bbq-admin",
            41.525000, -5.780000,
            List.of(NombreCategoria.BRASERIA, NombreCategoria.COMIDA_RAPIDA),
            Set.of(NombreRol.CLIENTE),
            List.of(
                plato("Costillas barbacoa",  14.00, "Costillas en salsa BBQ",       List.of()),
                plato("Alitas picantes",     9.00,  "Alitas con chile y miel",      List.of()),
                plato("Bocadillo de pulled pork", 8.50, "Cerdo desmechado",           List.of())
            )
        );

        crearRestaurante(
            "Café Central", "cafe-admin",
            41.530000, -5.760000,
            List.of(NombreCategoria.TRADICIONAL),
            Set.of(NombreRol.CLIENTE),
            List.of(
                plato("Café solo",        1.50, "Café expreso tradicional",    List.of()),
                plato("Tostada con tomate",2.00, "Pan con tomate y aceite",     List.of(NombreAlergeno.GLUTEN)),
                plato("Croissant",        2.50, "Masa hojaldrada",             List.of(NombreAlergeno.GLUTEN, NombreAlergeno.LACTEOS))
            )
        );

        crearRestaurante(
            "La Ruta de Tapas", "tapas-admin",
            41.515500, -5.755500,
            List.of(NombreCategoria.TRADICIONAL, NombreCategoria.MEDITERRANEO),
            Set.of(NombreRol.CLIENTE),
            List.of(
                plato("Patatas bravas",   4.50, "Patatas con salsa picante",      List.of()),
                plato("Gambas al ajillo", 11.00, "Gambas con ajo y guindilla",     List.of(NombreAlergeno.CRUSTACEOS)),
                plato("Pincho moruno",    3.00, "Brocheta de carne especiada",    List.of())
            )
        );

        crearRestaurante(
            "Restaurante El Mirador", "mirador-admin",
            41.650000, -5.800000, // ±15 km al noreste
            List.of(NombreCategoria.MEDITERRANEO, NombreCategoria.FUSION),
            Set.of(NombreRol.CLIENTE),
            List.of(
                plato("Paella de marisco", 14.50, "Arroz con marisco fresco", List.of(NombreAlergeno.CRUSTACEOS)),
                plato("Gazpacho andaluz",  5.00,  "Sopa fría de tomate",      List.of())
            )
        );

        crearRestaurante(
            "Taberna del Sur", "taberna-admin",
            41.360000, -5.600000, // ±17 km al sureste
            List.of(NombreCategoria.TRADICIONAL, NombreCategoria.CHINO),
            Set.of(NombreRol.CLIENTE),
            List.of(
                plato("Ramen de cerdo",     11.00, "Caldo intenso con panceta", List.of(NombreAlergeno.SOJA, NombreAlergeno.GLUTEN)),
                plato("Empanada gallega",    4.20, "Masa rellena de atún",      List.of(NombreAlergeno.GLUTEN, NombreAlergeno.PESCADO))
            )
);
    }

    // método getOrCreateOwner, plato(...) y crearRestaurante(...) sin cambios...
    private Usuario getOrCreateOwner(String username, Set<NombreRol> roles) {
        return usuarioService.findByUsername(username).orElseGet(() -> {
            Usuario u = Usuario.builder()
                    .username(username)
                    .nombre(username)
                    .email(username + "@eatelligence.com")
                    .telefonoMovil(TEL_PLACEHOLDER)
                    .password(usuarioService.encodePassword(DEFAULT_PASSWORD))
                    .verificado(true)
                    .build();

            roles.forEach(r ->
                u.getRoles().add(
                    rolService.findByNombre(r)
                              .orElseThrow(() -> new IllegalStateException("Rol " + r + " no existe"))
                )
            );

            return usuarioService.save(u);
        });
    }

    private Plato plato(String nombre, double precio,
                        String descripcion, List<NombreAlergeno> alergs) {
        Plato p = Plato.builder()
                .nombre(nombre)
                .descripcion(descripcion)
                .precio(precio)
                .ingredientes(descripcion)
                .activo(true)
                .build();

        alergs.forEach(a ->
            p.getAlergenos().add(
                alergenoService.findByNombre(a)
                               .orElseThrow(() -> new IllegalStateException("Alergeno " + a + " no existe"))
            )
        );
        return p;
    }

    /**
     * Creates and preloads a restaurant entity into the system if it does not already exist.
     *
     * @param comercial   the commercial name of the restaurant
     * @param username    the username of the restaurant owner
     * @param lat         the latitude of the restaurant's location
     * @param lon         the longitude of the restaurant's location
     * @param categorias  a list of categories the restaurant belongs to
     * @param roles       a set of roles assigned to the restaurant owner
     * @param platos      a list of dishes offered by the restaurant
     */
    private void crearRestaurante(
        String comercial, String username, double lat, double lon,
        List<NombreCategoria> categorias, Set<NombreRol> roles, List<Plato> platos
    ) {
        if (restauranteService.existsByNombreComercial(comercial)) {
            log.info("   → Restaurante '{}' ya existe, omitiendo creación", comercial);
            return;
        }

        Usuario owner = getOrCreateOwner(username, roles);

        Direccion dir = Direccion.builder()
                .numCalle("1")
                .calle("Calle Falsa")
                .ciudad("Zamora")
                .codigoPostal("49001")
                .provincia("Zamora")
                .latitud(lat)
                .longitud(lon)
                .build();

        Restaurante r = Restaurante.builder()
                .nombreComercial(comercial)
                .telefonoFijo("980000000")
                .emailEmpresa(username + "@eatelligence.com")
                .descripcion("Restaurante de ejemplo generado por bootstrap")
                .tiempoPreparacionEstimado(25)
                .importeMinimo(12.5)
                .verificado(true)
                .activo(true)
                .propietario(owner)
                .direccion(dir)
                .build();

        for (DayOfWeek d : DayOfWeek.values()) {
            r.getHorarios().add(Horario.builder()
                    .dia(d)
                    .apertura(LocalTime.of(0, 0))
                    .cierre(LocalTime.of(23, 59))
                    .build());
        }

        categorias.forEach(c ->
            r.getCategorias().add(
                categoriaService.findByNombre(c)
                                .orElseThrow(() -> new IllegalStateException("Categoría " + c + " no existe"))
            )
        );

        platos.forEach(p -> {
            p.setRestaurante(r);
            r.getPlatos().add(p);
        });

        restauranteService.save(r);
        log.info("   → Restaurante '{}' precargado", comercial);
    }
}
