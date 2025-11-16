package com.example.eva.service;

import com.example.eva.model.Rol;
import com.example.eva.model.Usuario;
import com.example.eva.model.UsuarioRol;
import com.example.eva.repository.RolRepository;
import com.example.eva.repository.UsuarioRepository;
import com.example.eva.repository.UsuarioRolRepository;
import com.example.eva.repository.InscripcionRepository;
import com.example.eva.repository.ValoracionRepository;
import com.example.eva.util.LoggerEva;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final InscripcionRepository inscripcionRepository;
    private final ValoracionRepository valoracionRepository;
    private final UsuarioRolRepository usuarioRolRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    private final LoggerEva logger = LoggerEva.getInstancia();  // <-- Singleton

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            InscripcionRepository inscripcionRepository,
            ValoracionRepository valoracionRepository,
            UsuarioRolRepository usuarioRolRepository,
            RolRepository rolRepository,
            PasswordEncoder passwordEncoder) {

        this.usuarioRepository = usuarioRepository;
        this.inscripcionRepository = inscripcionRepository;
        this.valoracionRepository = valoracionRepository;
        this.usuarioRolRepository = usuarioRolRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario guardar(Usuario usuario) {

        logger.info("Intentando guardar usuario: " + usuario.getCorreo());

        if (usuario.getContrasena() != null && !usuario.getContrasena().isBlank()) {
            logger.info("Cifrando contraseña del usuario.");
            usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));
        }

        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        logger.info("Usuario guardado exitosamente con ID: " + usuarioGuardado.getIdUser());

        try {
            if (usuarioGuardado.getUsuarioRoles() == null || usuarioGuardado.getUsuarioRoles().isEmpty()) {
                Rol rolUser = rolRepository.findByNombre("USER")
                        .orElseThrow(() -> new RuntimeException("Rol USER no encontrado en la base de datos"));

                UsuarioRol ur = new UsuarioRol();
                ur.setUsuario(usuarioGuardado);
                ur.setRol(rolUser);

                usuarioRolRepository.save(ur);

                logger.info("Rol USER asignado al usuario: " + usuarioGuardado.getCorreo());
            }
        } catch (Exception e) {
            logger.error("Error asignando rol: " + e.getMessage());
        }

        return usuarioGuardado;
    }

    public List<Usuario> listar() {
        logger.info("Listando todos los usuarios");
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> buscarPorId(Long id) {
        logger.info("Buscando usuario por ID: " + id);
        return usuarioRepository.findById(id);
    }

    public void eliminar(Long id) {
        logger.warning("Eliminando usuario con ID: " + id);
        usuarioRepository.deleteById(id);
    }

    public List<Usuario> buscar(String keyword) {
        logger.info("Buscando usuarios por keyword: " + keyword);
        if (keyword == null || keyword.isBlank()) {
            return usuarioRepository.findAll();
        }
        return usuarioRepository.searchByKeyword(keyword);
    }

    public List<Usuario> buscarConFiltros(String nombre, String correo, String estado, String documento,
                                          String telefono, String direccion) {

        logger.info("Filtrando usuarios con múltiples parámetros");

        return usuarioRepository.findAll().stream()
                .filter(u -> (nombre == null || u.getNombre().toLowerCase().contains(nombre.toLowerCase())))
                .filter(u -> (correo == null || u.getCorreo().toLowerCase().contains(correo.toLowerCase())))
                .filter(u -> (estado == null || (u.getEstado() != null && u.getEstado().toLowerCase().contains(estado.toLowerCase()))))
                .filter(u -> (documento == null || u.getDocumento().contains(documento)))
                .filter(u -> (telefono == null || (u.getTelefono() != null && u.getTelefono().toLowerCase().contains(telefono.toLowerCase()))))
                .filter(u -> (direccion == null || (u.getDireccion() != null && u.getDireccion().toLowerCase().contains(direccion.toLowerCase()))))
                .toList();
    }

    public Page<Usuario> buscarConFiltrosPaginado(String nombre, String correo, String estado, String documento,
                                                  String telefono, String direccion, Pageable pageable) {

        logger.info("Búsqueda paginada activada");

        List<Usuario> filtrados = buscarConFiltros(nombre, correo, estado, documento, telefono, direccion);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), filtrados.size());

        return new PageImpl<>(filtrados.subList(start, end), pageable, filtrados.size());
    }

    public boolean esAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.getAuthorities() != null) {
            return auth.getAuthorities().stream()
                    .anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN"));
        }
        return false;
    }
}
