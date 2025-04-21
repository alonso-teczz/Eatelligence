package com.alonso.eatelligence.service.imp;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.alonso.eatelligence.model.dto.ClienteRegistroDTO;
import com.alonso.eatelligence.model.dto.RestauranteRegistroDTO;
import com.alonso.eatelligence.model.entity.Direccion;
import com.alonso.eatelligence.model.entity.Restaurante;
import com.alonso.eatelligence.model.entity.Rol.NombreRol;
import com.alonso.eatelligence.model.entity.Usuario;
import com.alonso.eatelligence.repository.IRestauranteRepository;
import com.alonso.eatelligence.service.IEntitableClient;
import com.alonso.eatelligence.service.IRestauranteService;

@Service
public class ResturanteServiceImp implements IRestauranteService, IEntitableClient {

    @Autowired
    private IRestauranteRepository  restauranteRepository;

    @Autowired
    private RolServiceImp rolService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public String encodePassword(String password) {
        return this.passwordEncoder.encode(password);
    }

    public boolean matchesPassword(String rawPassword, String hashedPassword) {
        return this.passwordEncoder.matches(rawPassword, hashedPassword);
    }

    @Override
    public Usuario clientDTOtoEntity(ClienteRegistroDTO cliente) {
        Usuario usuario = Usuario.builder()
            .username(cliente.getUsername())
            .nombre(cliente.getNombre())
            .apellidos(cliente.getApellidos())
            .email(cliente.getEmail())
            .telefonoMovil(cliente.getTelefonoMovil())
            .password(this.encodePassword(cliente.getPassword()))
            .roles(
                List.of(
                    this.rolService.findByNombre(NombreRol.CLIENTE).orElseThrow(),
                    this.rolService.findByNombre(NombreRol.ADMIN).orElseThrow()
                )
            )
            .build();

        if (cliente.getDireccion() != null) {
            usuario.getDirecciones().add(
                Direccion.builder()
                .calle(cliente.getDireccion().getCalle())
                .numCalle(cliente.getDireccion().getNumCalle())
                .ciudad(cliente.getDireccion().getCiudad())
                .provincia(cliente.getDireccion().getProvincia())
                .codigoPostal(cliente.getDireccion().getCodigoPostal())
                .latitud(cliente.getDireccion().getLatitud())
                .longitud(cliente.getDireccion().getLongitud())
                .usuario(usuario)
                .build()
            );
        }

        return usuario;
    }

    public Restaurante restDTOtoEntity(RestauranteRegistroDTO restaurante) {
        Restaurante restauranteEntity = Restaurante.builder()
            .propietario(this.clientDTOtoEntity(restaurante.getPropietario()))
            .nombreComercial(restaurante.getNombreComercial())
            .descripcion(restaurante.getDescripcion())
            .telefonoFijo(restaurante.getTelefonoFijo())
            .emailEmpresa(restaurante.getEmailEmpresa())
            .direccion(
                Direccion.builder()
                .calle(restaurante.getDireccionRestaurante().getCalle())
                .numCalle(restaurante.getDireccionRestaurante().getNumCalle())
                .ciudad(restaurante.getDireccionRestaurante().getCiudad())
                .provincia(restaurante.getDireccionRestaurante().getProvincia())
                .codigoPostal(restaurante.getDireccionRestaurante().getCodigoPostal())
                .latitud(restaurante.getDireccionRestaurante().getLatitud())
                .longitud(restaurante.getDireccionRestaurante().getLongitud())
                .build()
            )
            .build();

        restauranteEntity.getDireccion().setRestaurante(restauranteEntity);

        return restauranteEntity;
    }

    @Override
    public Optional<Restaurante> findById(Long id) {
        return this.restauranteRepository.findById(id);
    }

    @Override
    public Optional<Restaurante> findByUsuario(Usuario usuario) {
        return this.restauranteRepository.findByPropietario(usuario);
    }

    @Override
    public List<Restaurante> getAllRestaurants() {
        return this.restauranteRepository.findAll();
    }

    @Override
    public Restaurante save(Restaurante restaurante) {
        return this.restauranteRepository.save(restaurante);
    }

    @Override
    public void update(Restaurante restaurante) {
        this.restauranteRepository.save(restaurante);
    }
    
    @Override
    public void deleteById(Long id) {
        this.restauranteRepository.deleteById(id);
    }
    
}
