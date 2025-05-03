package com.alonso.eatelligence.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.alonso.eatelligence.model.dto.CategoriaDTO;
import com.alonso.eatelligence.model.dto.HorarioDTO;
import com.alonso.eatelligence.model.entity.Restaurante;
import com.alonso.eatelligence.model.entity.Usuario;
import com.alonso.eatelligence.model.projection.ResumenProjection;

public interface IRestauranteService {
    Optional<Restaurante> findById(Long id);

    Optional<Restaurante> findByUsuario(Usuario usuario);

    List<CategoriaDTO> getCategoriasFromRestaurante(Long restauranteId);

    List<Restaurante> getAllRestaurants();

    Page<ResumenProjection> getAllRestaurantsWithFilters(
      String nombre,
      Double min,
      Double max,
      double lat,
      double lon,
      Integer radio,
      Set<Long> alergenos,
      Set<Long> categorias,
      Pageable pageable
    );  

    Set<HorarioDTO> obtenerHorarios(Long id);

    Restaurante save(Restaurante restaurante);

    void update(Restaurante restaurante);
    
    void actualizarHorarios(Long id, Set<HorarioDTO> horarios);

    void deleteById(Long id);

    void actualizarCategorias(Restaurante restaurante, List<Long> ids);
}
