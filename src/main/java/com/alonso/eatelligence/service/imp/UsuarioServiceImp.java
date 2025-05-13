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
import com.alonso.eatelligence.model.entity.UsuarioRol;
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
            .build();

            usuario.setRoles(List.of(
                UsuarioRol.builder().usuario(usuario).rol(this.rolService.findByNombre(NombreRol.CLIENTE).orElseThrow()).build()
            ));

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
    public Optional<Usuario> findByUsername(String username) {
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
    public void addRoleToUser(String username, NombreRol rolNombre) {
        Optional<Usuario> opt = this.usuarioRepository.findByUsername(username);

        if (opt.isEmpty()) {
            throw new IllegalArgumentException("El usuario no existe");
        }

        Rol rol = this.rolService.findByNombre(rolNombre)
            .orElseThrow(() -> new IllegalArgumentException("Rol no existe"));
        
        Usuario user = opt.get();

        // Verificar si ya tiene ese rol
        boolean hasRole = user.getRoles().stream()
            .anyMatch(ur -> ur.getRol().equals(rol));

        if (!hasRole) {
            UsuarioRol usuarioRol = UsuarioRol.builder()
                .usuario(user)
                .rol(rol)
                .build();

            user.getRoles().add(usuarioRol);
            this.usuarioRepository.save(user);
        }

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

    @Override
    public List<Usuario> findAllByRestauranteAsignadoAndRol(Restaurante restaurante, NombreRol rol) {
        return this.usuarioRepository.findAllByRestauranteAsignadoAndRolesNombre(restaurante, rol);
    }

}
