package com.alonso.eatelligence.service.imp;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.alonso.eatelligence.model.dto.CategoriaDTO;
import com.alonso.eatelligence.model.dto.ClienteRegistroDTO;
import com.alonso.eatelligence.model.dto.HorarioDTO;
import com.alonso.eatelligence.model.dto.RestauranteRegistroDTO;
import com.alonso.eatelligence.model.entity.Categoria;
import com.alonso.eatelligence.model.entity.Direccion;
import com.alonso.eatelligence.model.entity.Horario;
import com.alonso.eatelligence.model.entity.NombreRol;
import com.alonso.eatelligence.model.entity.Restaurante;
import com.alonso.eatelligence.model.entity.Usuario;
import com.alonso.eatelligence.model.projection.ResumenProjection;
import com.alonso.eatelligence.repository.ICategoriaRepository;
import com.alonso.eatelligence.repository.IRestauranteRepository;
import com.alonso.eatelligence.service.IEntitableClient;
import com.alonso.eatelligence.service.IRestauranteService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class RestauranteServiceImp implements IRestauranteService, IEntitableClient {

    // private static Logger logger = LogManager.getLogger(RestauranteServiceImp.class);

    @Autowired
    private IRestauranteRepository restauranteRepository;

    @Autowired
    private RolServiceImp rolService;

    @Autowired
    private ICategoriaRepository categoriaRepository;

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
            .roles(Set.of(
                this.rolService.findByNombre(NombreRol.CLIENTE).orElseThrow(),
                this.rolService.findByNombre(NombreRol.ADMIN).orElseThrow()
            ))
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
    public void actualizarHorarios(Long restauranteId, List<HorarioDTO> horario) {
        this.restauranteRepository.findById(restauranteId).ifPresent(r -> {
            List<Horario> nuevoHorario = horario.stream()
                .map(h -> Horario.builder()
                    .dia(h.getDia())
                    .apertura(h.getApertura())
                    .cierre(h.getCierre())
                    .build()
                ).collect(Collectors.toList());
    
            r.setHorarios(nuevoHorario);
            this.restauranteRepository.save(r);
            return;
        });

        // logger.warn("Se intentó actualizar el horario de un restaurante no encontrado");
        return;
    }

    @Override
    @Transactional
    public List<HorarioDTO> obtenerHorarios(Long id) {
      Restaurante r = this.restauranteRepository.findById(id).get();
      return r.getHorarios().stream()
        .map(h -> {
            return HorarioDTO.builder()
            .apertura(h.getApertura())
            .cierre(h.getCierre())
            .dia(h.getDia())
            .build();
        })
        .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void actualizarCategorias(Restaurante restaurante, List<Long> categoriaIds) {
        List<Categoria> categorias = this.categoriaRepository.findAllById(categoriaIds);
        restaurante.setCategorias(new HashSet<>(categorias));
        restauranteRepository.save(restaurante);
    }

    public List<CategoriaDTO> getCategoriasFromRestaurante(Long restauranteId) {
        Restaurante restaurante = restauranteRepository.findById(restauranteId)
            .orElseThrow(() -> new EntityNotFoundException("Restaurante no encontrado"));
        
        return restaurante.getCategorias().stream()
            .map(cat -> new CategoriaDTO(cat.getId(), cat.getSerialName()))
            .toList();
    }

    @Override
    public Page<ResumenProjection> getAllRestaurantsWithFilters(String nombre, Double min, Double max, double lat,
            double lon, Integer radio, Set<Long> alergenos, Set<Long> categorias, DayOfWeek dia, LocalTime hora,
            Pageable pageable) {
        return this.restauranteRepository.getAllRestaurantsWithFilters(nombre, min, max, lat, lon, radio, alergenos, categorias, dia, hora, pageable);
    }

    public boolean existsByNombreComercial(String nombreComercial) {
        return this.restauranteRepository.findByNombreComercial(nombreComercial).isPresent();
    }
   
}
