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
import java.util.stream.Collectors;

import org.springframework.data.domain.*;
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

    private final LoggerEva logger = LoggerEva.getInstancia();

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

    // 🔥 MÉTODO PRINCIPAL CORREGIDO
    public Usuario guardar(Usuario usuario) {

        logger.info("Intentando guardar usuario: " + usuario.getCorreo());

        // 🔒 MANEJO SEGURO DE CONTRASEÑA
        if (usuario.getIdUser() != null) {

            Usuario existente = usuarioRepository.findById(usuario.getIdUser()).orElse(null);

            if (existente != null) {

                // ❌ SI VIENE VACÍA → NO TOCAR CONTRASEÑA
                if (usuario.getContrasena() == null || usuario.getContrasena().isBlank()) {
                    usuario.setContrasena(existente.getContrasena());
                } else {
                    // 🔐 SOLO CIFRAR SI NO ESTÁ CIFRADA
                    if (!usuario.getContrasena().startsWith("$2a$")) {
                        usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));
                    }
                }
            }
        } else {
            // 🆕 NUEVO USUARIO → CIFRAR
            if (usuario.getContrasena() != null && !usuario.getContrasena().isBlank()) {
                usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));
            }
        }

        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        logger.info("Usuario guardado con ID: " + usuarioGuardado.getIdUser());

        // 🔥 ASIGNAR ROL SI NO TIENE
        try {
            if (usuarioGuardado.getUsuarioRoles() == null || usuarioGuardado.getUsuarioRoles().isEmpty()) {

                Rol rolUser = rolRepository.findByNombre("USER")
                        .orElseThrow(() -> new RuntimeException("Rol USER no encontrado"));

                UsuarioRol ur = new UsuarioRol();
                ur.setUsuario(usuarioGuardado);
                ur.setRol(rolUser);

                usuarioRolRepository.save(ur);
            }
        } catch (Exception e) {
            logger.error("Error asignando rol: " + e.getMessage());
        }

        return usuarioGuardado;
    }

    // 🔥 ESTE ERA CLAVE PARA TU ERROR
    public Usuario obtenerUsuarioLogueado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
            return null;
        }

        return usuarioRepository.findByCorreoFetchRoles(auth.getName()).orElse(null);
    }

    // 🔥 ESTE ERA EL PROBLEMA DEL esAdmin()
    public boolean esAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) return false;

        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    // ===== RESTO DEL SISTEMA (SIN CAMBIOS IMPORTANTES) =====

    public List<Usuario> listar() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    public void eliminar(Long id) {
        usuarioRepository.deleteById(id);
    }

    public Page<Usuario> buscarConFiltrosPaginado(String nombre, String correo, String estado, String documento,
            String telefono, String direccion, Pageable pageable) {

        try {
            return usuarioRepository.searchWithFilters(
                    normalizeOrNull(nombre),
                    normalizeOrNull(correo),
                    normalizeOrNull(estado),
                    normalizeOrNull(documento),
                    normalizeOrNull(telefono),
                    normalizeOrNull(direccion),
                    pageable
            );
        } catch (Exception ex) {
            List<Usuario> filtrados = usuarioRepository.findAll();
            return new PageImpl<>(filtrados, pageable, filtrados.size());
        }
    }

    private String normalizeOrNull(String s) {
        if (s == null || s.trim().isEmpty()) return null;
        return s.toLowerCase();
    }
}