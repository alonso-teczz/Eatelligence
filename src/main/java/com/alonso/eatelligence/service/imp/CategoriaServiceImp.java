package com.alonso.eatelligence.service.imp;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alonso.eatelligence.model.entity.Categoria;
import com.alonso.eatelligence.model.entity.Categoria.NombreCategoria;
import com.alonso.eatelligence.repository.ICategoriaRepository;
import com.alonso.eatelligence.service.ICategoriaService;

@Service
public class CategoriaServiceImp implements ICategoriaService {
    @Autowired
    private ICategoriaRepository categoriaRepository;

    @Override
    public Optional<Categoria> findByNombre(NombreCategoria nombre) {
        return this.categoriaRepository.findByNombre(nombre);
    }

    @Override
    public List<Categoria> getAll() {
        return this.categoriaRepository.findAll();
    }

    @Override
    public Categoria save(Categoria categoria) {
       return this.categoriaRepository.save(categoria);
    }
    
}
