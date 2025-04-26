package com.alonso.eatelligence.service.imp;

import com.alonso.eatelligence.model.dto.PlatoDTO;
import com.alonso.eatelligence.model.entity.Alergeno;
import com.alonso.eatelligence.model.entity.Plato;
import com.alonso.eatelligence.model.entity.Restaurante;
import com.alonso.eatelligence.repository.IAlergenoRepository;
import com.alonso.eatelligence.repository.IPlatoRepository;
import com.alonso.eatelligence.service.IPlatoService;
import jakarta.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class PlatoServiceImp implements IPlatoService {

    @Autowired
    private IPlatoRepository platoRepository;

    @Autowired
    private IAlergenoRepository alergenoRepository;

    @Override
    public List<Plato> getAll() {
        return this.platoRepository.findAll();
    }

    @Override
    public Plato findyById(Long id) {
        return this.platoRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Plato no encontrado con ID: " + id));
    }

    @Override
    public Plato saveFromDTO(PlatoDTO plato, Restaurante restaurante) {
        return this.platoRepository.save(this.mapToEntity(plato, restaurante));
    }

    @Override
    public Plato updateFromDTO(Long id, PlatoDTO plato) {
        Plato existente = this.platoRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Plato no encontrado con ID: " + id));

        existente.setNombre(plato.getNombre());
        existente.setDescripcion(plato.getDescripcion());
        existente.setIngredientes(plato.getIngredientes());
        existente.setPrecio(plato.getPrecio());

        Set<Alergeno> alergenos = this.alergenoRepository.findByIdIn(plato.getAlergenos());
        existente.setAlergenos(alergenos);

        return this.platoRepository.save(existente);
    }

    @Override
    public void delete(Long id) {
        if (!this.platoRepository.existsById(id)) {
            throw new EntityNotFoundException("Plato no encontrado con ID: " + id);
        }
        this.platoRepository.deleteById(id);
    }

    private Plato mapToEntity(PlatoDTO plato, Restaurante restaurante) {
        Set<Alergeno> alergenos = this.alergenoRepository.findByIdIn(plato.getAlergenos());

        return Plato.builder()
            .nombre(plato.getNombre())
            .descripcion(plato.getDescripcion())
            .precio(plato.getPrecio())
            .alergenos(alergenos)
            .restaurante(restaurante)
            .ingredientes(plato.getIngredientes())
            .build();
    }

    @Override
    public long countAll() {
        return this.platoRepository.count();
    }
}