package com.alonso.eatelligence.repository;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Optional;
import java.util.Set;

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
    SELECT  r.id                          AS id,
            r.nombre_comercial           AS nombreComercial,
            r.importe_minimo             AS importeMinimo,
            r.tiempo_preparacion_estimado AS tiempoPreparacion,
            (SELECT AVG(p2.precio) FROM platos p2 WHERE p2.restaurante_id = r.id) AS precioMedio,
            d.ciudad, d.provincia, d.latitud, d.longitud,
            CONCAT(
              LPAD(TIME_FORMAT(h.apertura,'%H:%i'),5,'0'), '–', LPAD(TIME_FORMAT(h.cierre,'%H:%i'),5,'0')
            ) AS horarioDia,
            TRUE AS abiertoAhora
      FROM restaurantes r
      JOIN direcciones d ON r.direccion_id = d.id
      JOIN horarios h ON h.restaurante_id = r.id AND h.dia = :dia
        AND ((h.apertura < h.cierre AND :hora BETWEEN h.apertura AND h.cierre)
          OR (h.apertura >= h.cierre AND (:hora >= h.apertura OR :hora < h.cierre)))
     WHERE (:nombre IS NULL OR r.nombre_comercial LIKE CONCAT('%', :nombre, '%'))
       AND (:min IS NULL OR r.importe_minimo >= :min)
       AND (:max IS NULL OR r.importe_minimo <= :max)
       AND (:alergenos IS NULL OR NOT EXISTS (
              SELECT 1 FROM platos p JOIN plato_alergeno pa ON pa.plato_id = p.id
               WHERE p.restaurante_id = r.id AND pa.alergeno_id IN (:alergenos)
           ))
       AND (:categorias IS NULL OR EXISTS (
              SELECT 1 FROM restaurante_categoria rc
               WHERE rc.restaurante_id = r.id AND rc.categoria_id IN (:categorias)
           ))
       /* distancia en metros: radio(km)*1000 */
       AND ST_Distance_Sphere(
             POINT(:lon, :lat), POINT(d.longitud, d.latitud)
           ) <= (:radio * 1000)
     ORDER BY ST_Distance_Sphere(POINT(:lon, :lat), POINT(d.longitud, d.latitud))
    """,
    countQuery = """
      SELECT COUNT(*)
        FROM restaurantes r
        JOIN direcciones d ON r.direccion_id = d.id
        JOIN horarios h ON h.restaurante_id = r.id AND h.dia = :dia
          AND ((h.apertura < h.cierre AND :hora BETWEEN h.apertura AND h.cierre)
            OR (h.apertura >= h.cierre AND (:hora >= h.apertura OR :hora < h.cierre)))
       WHERE (:nombre IS NULL OR r.nombre_comercial LIKE CONCAT('%', :nombre, '%'))
         AND (:min IS NULL OR r.importe_minimo >= :min)
         AND (:max IS NULL OR r.importe_minimo <= :max)
         AND (:alergenos IS NULL OR NOT EXISTS (
                SELECT 1 FROM platos p JOIN plato_alergeno pa ON pa.plato_id = p.id
                 WHERE p.restaurante_id = r.id AND pa.alergeno_id IN (:alergenos)
             ))
         AND (:categorias IS NULL OR EXISTS (
                SELECT 1 FROM restaurante_categoria rc
                 WHERE rc.restaurante_id = r.id AND rc.categoria_id IN (:categorias)
             ))
         AND ST_Distance_Sphere(
               POINT(:lon, :lat), POINT(d.longitud, d.latitud)
             ) <= (:radio * 1000)
    """,
    nativeQuery = true)
  Page<ResumenProjection> getAllRestaurantsWithFilters(
      @Param("nombre") String nombre,
      @Param("min") Double min,
      @Param("max") Double max,
      @Param("lat") double lat,
      @Param("lon") double lon,
      @Param("radio") Integer radio,
      @Param("alergenos") Set<Long> alergenos,
      @Param("categorias") Set<Long> categorias,
      @Param("dia") DayOfWeek dia,
      @Param("hora") LocalTime hora,
      Pageable pageable);
  

  Optional<Restaurante> findByNombreComercial(String nombreComercial);

}
