package com.alonso.eatelligence.service.imp;

import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alonso.eatelligence.model.entity.Alergeno;
import com.alonso.eatelligence.model.entity.Alergeno.NombreAlergeno;
import com.alonso.eatelligence.repository.IAlergenoRepository;
import com.alonso.eatelligence.service.IAlergenoService;

@Service
public class AlergenoServiceImp implements IAlergenoService {

    @Autowired
    private IAlergenoRepository alergenoRepository;

    @Override
    public Alergeno save(Alergeno alergeno) {
        return this.alergenoRepository.save(alergeno);
    }

    @Override
    public Optional<Alergeno> findByNombre(NombreAlergeno nombre) {
        return this.alergenoRepository.findByNombre(nombre);
    }

    public int count() {
        return (int) this.alergenoRepository.count();
    }

    @Override
    public Set<Alergeno> getAll() {
        return Set.copyOf(this.alergenoRepository.findAll());
    }
    
}
