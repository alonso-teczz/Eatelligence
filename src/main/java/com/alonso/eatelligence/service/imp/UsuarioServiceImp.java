package com.alonso.eatelligence.service.imp;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.alonso.eatelligence.model.dto.ClienteRegistroDTO;
import com.alonso.eatelligence.model.entity.Direccion;
import com.alonso.eatelligence.model.entity.NombreRol;
import com.alonso.eatelligence.model.entity.Restaurante;
import com.alonso.eatelligence.model.entity.Rol;
import com.alonso.eatelligence.model.entity.Usuario;
import com.alonso.eatelligence.repository.IUsuarioRepository;
import com.alonso.eatelligence.service.IEntitableClient;
import com.alonso.eatelligence.service.IUsuarioService;

import jakarta.transaction.Transactional;

@Service
public class UsuarioServiceImp implements IUsuarioService, IEntitableClient {

    @Autowired
    private IUsuarioRepository usuarioRepository;

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
                    this.rolService.findByNombre(NombreRol.CLIENTE).orElseThrow()
                )
            )
            .build();

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
        
        return usuario;
    }

    @Override
    public Usuario save(Usuario usuario) {
        return this.usuarioRepository.save(usuario);
    }

    @Override
    public void update(Usuario usuario) {
        this.usuarioRepository.save(usuario);
    }

    @Override
    public Optional<Usuario> findById(Long id) {
        return this.usuarioRepository.findById(id);
    }

    @Override
    public List<Usuario> getAllUsers() {
        return this.usuarioRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        this.usuarioRepository.deleteById(id);
    }

    @Override
    public boolean existsByUsername(String nombre) {
        return this.usuarioRepository.existsByUsername(nombre);
    }

    @Override
    public Usuario findByUsername(String username) {
        return this.usuarioRepository.findByUsername(username);
    }

    @Override
    public boolean checkPassword(Usuario u, String password) {
        return this.matchesPassword(password, u.getPassword());
    }

    @Override
    public Optional<Usuario> findByUsernameAndEmail(String username, String email) {
        return this.usuarioRepository.findByUsernameAndEmail(username, email);
    }

    @Transactional
    @Override
    public void addRoleToUser(String username, NombreRol rolNombre) {
        Usuario user = this.usuarioRepository.findByUsername(username);

        Rol rol = this.rolService.findByNombre(rolNombre)
            .orElseThrow(() -> new IllegalArgumentException("Rol no existe: " + rolNombre));

        user.getRoles().add(rol);
        this.usuarioRepository.save(user);
    }

    @Override
    public void asignarRestaurante(Usuario usuario, Restaurante r) {
        usuario.setRestauranteAsignado(r);
        this.usuarioRepository.save(usuario);
    }

    @Override
    public long countByRestauranteAsignadoAndRol(Restaurante restaurante, NombreRol rol) {
        return this.usuarioRepository.countByRestauranteAsignadoAndRol(restaurante, rol);
    }

}
