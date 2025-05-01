package com.alonso.eatelligence.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.alonso.eatelligence.model.entity.Restaurante;
import com.alonso.eatelligence.model.entity.Usuario;
import com.alonso.eatelligence.model.projection.ResumenProjection;

public interface IRestauranteRepository extends JpaRepository<Restaurante, Long> {
    Optional<Restaurante> findByPropietario(Usuario propietario);
    
    /** Consulta con filtros + cálculo de distancia y precio medio */
    @Query(value = """
        SELECT
          r.id                              AS id,
          r.nombre_comercial                AS nombreComercial,
          AVG(p.precio)                     AS precioMedio,
          ST_Distance_Sphere(
            Point(:lon, :lat),
            Point(d.longitud, d.latitud)
          ) / 1000.0                        AS distancia
        FROM restaurantes r
          JOIN platos p    ON p.restaurante_id = r.id
          JOIN direcciones d ON d.id = r.direccion_id
        WHERE ( :nombre IS NULL OR LOWER(r.nombre_comercial) LIKE LOWER(CONCAT('%', :nombre, '%')) )
        GROUP BY r.id, r.nombre_comercial, d.latitud, d.longitud
        HAVING ( :min IS NULL   OR AVG(p.precio) >= :min )
           AND ( :max IS NULL   OR AVG(p.precio) <= :max )
           AND ( :radio IS NULL OR ST_Distance_Sphere(
                 Point(:lon, :lat),
                 Point(d.longitud, d.latitud)
               ) / 1000.0 <= :radio )
        """,
        countQuery = """
        SELECT COUNT(DISTINCT r.id)
        FROM restaurantes r
          JOIN direcciones d ON d.id = r.direccion_id
        WHERE ( :nombre IS NULL OR LOWER(r.nombre_comercial) LIKE LOWER(CONCAT('%', :nombre, '%')) )
          AND ( :radio  IS NULL OR ST_Distance_Sphere(
                 Point(:lon, :lat),
                 Point(d.longitud, d.latitud)
               ) / 1000.0 <= :radio )
        """,
        nativeQuery = true
    )
    Page<ResumenProjection> getAllRestaurantsWithFilters(
        @Param("nombre") String nombre,
        @Param("min")    Double min,
        @Param("max")    Double max,
        @Param("lat")    double lat,
        @Param("lon")    double lon,
        @Param("radio")  Double radio,
        Pageable pageable
    );
    
}
