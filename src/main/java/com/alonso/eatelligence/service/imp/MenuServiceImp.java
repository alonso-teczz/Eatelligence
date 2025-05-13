package com.alonso.eatelligence.service.imp;

import com.alonso.eatelligence.model.entity.OpcionMenu;
import com.alonso.eatelligence.repository.IMenuRepository;
import com.alonso.eatelligence.service.IMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MenuServiceImp implements IMenuService {

    @Autowired
    private IMenuRepository menuRepository;

    @Override
    public List<OpcionMenu> obtenerOpcionesParaUsuario(String username) {
        return this.menuRepository.findOpcionesByUsername(username);
    }

    @Override
    public Optional<OpcionMenu> findByUrl(String ruta) {
        return this.menuRepository.findByUrl(ruta);
    }    

    @Override
    public Object save(OpcionMenu opcion) {
        return this.menuRepository.save(opcion);
    }
}
