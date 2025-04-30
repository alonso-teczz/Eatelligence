package com.alonso.eatelligence.service.imp;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.alonso.eatelligence.model.dto.ClienteRegistroDTO;
import com.alonso.eatelligence.model.dto.HorarioDTO;
import com.alonso.eatelligence.model.dto.RestauranteRegistroDTO;
import com.alonso.eatelligence.model.entity.Direccion;
import com.alonso.eatelligence.model.entity.Horario;
import com.alonso.eatelligence.model.entity.NombreRol;
import com.alonso.eatelligence.model.entity.Restaurante;
import com.alonso.eatelligence.model.entity.Usuario;
import com.alonso.eatelligence.repository.IRestauranteRepository;
import com.alonso.eatelligence.service.IEntitableClient;
import com.alonso.eatelligence.service.IRestauranteService;

import jakarta.transaction.Transactional;

@Service
public class RestauranteServiceImp implements IRestauranteService, IEntitableClient {

    private static Logger logger = LogManager.getLogger(RestauranteServiceImp.class);

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
                Set.of(
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

    public List<Restaurante> findAll() {
        return this.restauranteRepository.findAll();
    }

    @Override
    @Transactional
    public void actualizarHorarios(Long restauranteId, Set<HorarioDTO> horario) {
        this.restauranteRepository.findById(restauranteId).ifPresent(r -> {
            Set<Horario> nuevoHorario = horario.stream()
                .map(h -> Horario.builder()
                    .dia(h.getDia())
                    .apertura(h.getApertura())
                    .cierre(h.getCierre())
                    .build()
                ).collect(Collectors.toSet());
    
            r.setHorarios(nuevoHorario);
            this.restauranteRepository.save(r);
            return;
        });

        logger.warn("Se intentó actualizar el horario de un restaurante no encontrado");
        return;
    }

    @Override
    @Transactional
    public Set<HorarioDTO> obtenerHorarios(Long id) {
      Restaurante r = this.restauranteRepository.findById(id).get();
      return r.getHorarios().stream()
        .map(h -> new HorarioDTO(h.getDia(),h.getApertura(),h.getCierre()))
        .collect(Collectors.toSet());
    }
    
}
