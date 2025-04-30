package com.alonso.eatelligence.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.alonso.eatelligence.model.dto.HorarioDTO;
import com.alonso.eatelligence.model.entity.Restaurante;
import com.alonso.eatelligence.model.entity.Usuario;

public interface IRestauranteService {
    Optional<Restaurante> findById(Long id);

    Optional<Restaurante> findByUsuario(Usuario usuario);

    List<Restaurante> getAllRestaurants();

    Set<HorarioDTO> obtenerHorarios(Long id);

    Restaurante save(Restaurante restaurante);

    void update(Restaurante restaurante);
    
    void actualizarHorarios(Long id, Set<HorarioDTO> horarios);

    void deleteById(Long id);

}
