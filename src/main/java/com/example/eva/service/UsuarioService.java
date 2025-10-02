package com.example.eva.service;

import com.example.eva.model.Usuario;
import com.example.eva.repository.UsuarioRepository;
import com.example.eva.repository.InscripcionRepository;
import com.example.eva.repository.ValoracionRepository;
import com.example.eva.repository.UsuarioRolRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final InscripcionRepository inscripcionRepository;
    private final ValoracionRepository valoracionRepository;
    private final UsuarioRolRepository usuarioRolRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          InscripcionRepository inscripcionRepository,
                          ValoracionRepository valoracionRepository,
                          UsuarioRolRepository usuarioRolRepository,
                          PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.inscripcionRepository = inscripcionRepository;
        this.valoracionRepository = valoracionRepository;
        this.usuarioRolRepository = usuarioRolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 🔍 Búsqueda rápida por keyword
    public List<Usuario> buscar(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return usuarioRepository.findAll();
        }
        return usuarioRepository.searchByKeyword(keyword);
    }

    // 🔍 Búsqueda multicriterio (sin paginación)
    public List<Usuario> buscarConFiltros(String nombre, String correo, String estado, String documento) {
        Specification<Usuario> spec = construirFiltros(nombre, correo, estado, documento);
        return usuarioRepository.findAll(spec);
    }

    // 🔍 Búsqueda multicriterio con paginación
    public Page<Usuario> buscarConFiltrosPaginado(String nombre, String correo, String estado, String documento, Pageable pageable) {
        Specification<Usuario> spec = construirFiltros(nombre, correo, estado, documento);
        return usuarioRepository.findAll(spec, pageable);
    }

    // 🏗️ Método auxiliar para construir los filtros dinámicos
    private Specification<Usuario> construirFiltros(String nombre, String correo, String estado, String documento) {
        Specification<Usuario> spec = Specification.where(null);

        if (nombre != null && !nombre.isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("nombre")), "%" + nombre.toLowerCase() + "%"));
        }

        if (correo != null && !correo.isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("correo")), "%" + correo.toLowerCase() + "%"));
        }

        if (estado != null && !estado.isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("estado")), "%" + estado.toLowerCase() + "%"));
        }

        if (documento != null && !documento.isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(root.get("documento").as(String.class), "%" + documento + "%"));
        }

        return spec;
    }

    // 💾 Guardar usuario (encripta password si se envía)
    public Usuario guardar(Usuario usuario) {
        if (usuario.getContrasena() != null && !usuario.getContrasena().isBlank()) {
            usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));
        }
        return usuarioRepository.save(usuario);
    }

    // 🔎 Buscar usuario por ID
    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    // 🗑️ Eliminar usuario y sus relaciones
    public void eliminar(Long id) {
        inscripcionRepository.deleteByUsuarioIdUser(id);
        valoracionRepository.deleteByUsuarioIdUser(id);
        usuarioRolRepository.deleteByUsuarioIdUser(id);
        usuarioRepository.deleteById(id);
    }

    // 📋 Listar todos los usuarios
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    // 📋 Listar todos con paginación
    public Page<Usuario> listarTodosPaginado(Pageable pageable) {
        return usuarioRepository.findAll(pageable);
    }
}
