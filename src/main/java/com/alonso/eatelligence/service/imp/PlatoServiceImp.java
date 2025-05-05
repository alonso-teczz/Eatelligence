package com.alonso.eatelligence.service.imp;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alonso.eatelligence.model.dto.AltaPlatoDTO;
import com.alonso.eatelligence.model.entity.Alergeno;
import com.alonso.eatelligence.model.entity.Plato;
import com.alonso.eatelligence.model.entity.Restaurante;
import com.alonso.eatelligence.repository.IAlergenoRepository;
import com.alonso.eatelligence.repository.IPlatoRepository;
import com.alonso.eatelligence.service.IPlatoService;

import jakarta.persistence.EntityNotFoundException;

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
    public Plato saveFromDTO(AltaPlatoDTO plato, Restaurante restaurante) {
        return this.platoRepository.save(this.mapToEntity(plato, restaurante));
    }

    @Override
    public Plato updateFromDTO(Long id, AltaPlatoDTO plato) {
        Plato existente = this.platoRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Plato no encontrado con ID: " + id));

        existente.setNombre(plato.getNombre());
        existente.setDescripcion(plato.getDescripcion());
        existente.setIngredientes(plato.getIngredientes());
        existente.setPrecio(plato.getPrecio());
        existente.setLimiteUnidadesDiarias(plato.getLimiteUnidadesDiarias());

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

    private Plato mapToEntity(AltaPlatoDTO plato, Restaurante restaurante) {
        Set<Alergeno> alergenos = this.alergenoRepository.findByIdIn(plato.getAlergenos());

        return Plato.builder()
            .nombre(plato.getNombre())
            .descripcion(plato.getDescripcion())
            .precio(plato.getPrecio())
            .alergenos(alergenos)
            .restaurante(restaurante)
            .ingredientes(plato.getIngredientes())
            .limiteUnidadesDiarias(plato.getLimiteUnidadesDiarias())
            .build();
    }

    @Override
    public long countAll() {
        return this.platoRepository.count();
    }

    @Override
    public long countByRestaurante(Restaurante restaurante) {
        return this.platoRepository.countByRestaurante(restaurante);
    }

    @Override
    public void save(Plato p) {
        this.platoRepository.save(p);
    }

    @Override
    public Optional<Plato> findById(Long platoId) {
        return this.platoRepository.findById(platoId);
    }

    @Override
    public List<Plato> findByRestauranteId(Long id) {
        return this.platoRepository.findByRestauranteId(id);
    }

}