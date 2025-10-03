package com.example.eva.service;

import com.example.eva.model.Rol;
import com.example.eva.model.Usuario;
import com.example.eva.model.UsuarioRol;
import com.example.eva.repository.RolRepository;
import com.example.eva.repository.UsuarioRepository;
import com.example.eva.repository.UsuarioRolRepository;
import com.example.eva.repository.InscripcionRepository;
import com.example.eva.repository.ValoracionRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

// 📑 imports extra para paginación
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

// 📑 imports extra para seguridad
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

    public UsuarioService(UsuarioRepository usuarioRepository,
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

    // Guardar usuario con rol por defecto
    public Usuario guardar(Usuario usuario) {
        if (usuario.getContrasena() != null && !usuario.getContrasena().isBlank()) {
            usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));
        }

        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        // Asignar rol USER si no tiene roles
        if (usuarioGuardado.getUsuarioRoles() == null || usuarioGuardado.getUsuarioRoles().isEmpty()) {
            Rol rolUser = rolRepository.findByNombre("USER")
                    .orElseThrow(() -> new RuntimeException("⚠️ Rol USER no encontrado en la BD"));
            UsuarioRol ur = new UsuarioRol();
            ur.setUsuario(usuarioGuardado);
            ur.setRol(rolUser);
            usuarioRolRepository.save(ur);
        }
        return usuarioGuardado;
    }

    public List<Usuario> listar() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    public void eliminar(Long id) {
        usuarioRepository.deleteById(id);
    }

    // 🔍 Búsqueda por keyword usando el repositorio ya definido
    public List<Usuario> buscar(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return usuarioRepository.findAll();
        }
        return usuarioRepository.searchByKeyword(keyword);
    }

    // 🔍 Búsqueda con filtros múltiples (AMPLIADA con teléfono y dirección)
    public List<Usuario> buscarConFiltros(String nombre, String correo, String estado, String documento,
                                          String telefono, String direccion) {
        return usuarioRepository.findAll().stream()
                .filter(u -> (nombre == null || u.getNombre().toLowerCase().contains(nombre.toLowerCase())))
                .filter(u -> (correo == null || u.getCorreo().toLowerCase().contains(correo.toLowerCase())))
                .filter(u -> (estado == null || (u.getEstado() != null && u.getEstado().toLowerCase().contains(estado.toLowerCase()))))
                .filter(u -> (documento == null || u.getDocumento().contains(documento)))
                .filter(u -> (telefono == null || (u.getTelefono() != null && u.getTelefono().toLowerCase().contains(telefono.toLowerCase()))))
                .filter(u -> (direccion == null || (u.getDireccion() != null && u.getDireccion().toLowerCase().contains(direccion.toLowerCase()))))
                .toList();
    }

    // 📑 NUEVO: búsqueda con paginación extendida
    public Page<Usuario> buscarConFiltrosPaginado(String nombre, String correo, String estado, String documento,
                                                  String telefono, String direccion, Pageable pageable) {
        List<Usuario> filtrados = buscarConFiltros(nombre, correo, estado, documento, telefono, direccion);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), filtrados.size());
        return new PageImpl<>(filtrados.subList(start, end), pageable, filtrados.size());
    }

    // 📑 NUEVO: Método para saber si el usuario autenticado es ADMIN
    public boolean esAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getAuthorities() != null) {
            return auth.getAuthorities().stream()
                    .anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN"));
        }
        return false;
    }

    // 📑 NUEVO: Método que retorna solo la info permitida según el rol
    public List<Usuario> listarSegunRol() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        if (esAdmin()) {
            return usuarios; // ADMIN ve todo
        } else {
            // Si es USER, limpiamos campos sensibles
            return usuarios.stream().map(u -> {
                Usuario safeUser = new Usuario();
                safeUser.setIdUser(u.getIdUser());
                safeUser.setNombre(u.getNombre());
                safeUser.setCorreo(u.getCorreo());
                safeUser.setEstado(u.getEstado());
                // ❌ No exponemos documento, dirección, teléfono a USER
                return safeUser;
            }).toList();
        }
    }

    // ✅ AGREGADO: compatibilidad con controladores que usan searchByKeyword
    public List<Usuario> searchByKeyword(String keyword) {
        return buscar(keyword);
    }

    // ✅ AGREGADO: compatibilidad con controladores que usan searchWithFilters
    public Page<Usuario> searchWithFilters(String nombre, String correo, String estado, String documento,
                                           String telefono, String direccion, Pageable pageable) {
        return buscarConFiltrosPaginado(nombre, correo, estado, documento, telefono, direccion, pageable);
    }

    // ✅ AGREGADO: sobrecarga para compatibilidad con UsuarioViewController
    public Page<Usuario> buscarConFiltrosPaginado(String nombre, String correo, String estado, String documento, Pageable pageable) {
        return buscarConFiltrosPaginado(nombre, correo, estado, documento, null, null, pageable);
    }
}
