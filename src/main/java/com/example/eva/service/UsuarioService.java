package com.example.eva.service;

import com.example.eva.model.Usuario;
import com.example.eva.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Listar todos
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    // Guardar (nuevo o editar) → contraseña siempre encriptada
    public Usuario guardar(Usuario usuario) {
        if (usuario.getContrasena() != null && !usuario.getContrasena().isBlank()) {
            usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));
        } else {
            // si no se pasa contraseña nueva en edición, se conserva la existente
            usuarioRepository.findById(usuario.getIdUser())
                    .ifPresent(u -> usuario.setContrasena(u.getContrasena()));
        }
        return usuarioRepository.save(usuario);
    }

    // Buscar por ID
    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    // Eliminar por ID
    public void eliminar(Long id) {
        usuarioRepository.deleteById(id);
    }

    // Buscar por correo
    public Optional<Usuario> buscarPorCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo);
    }

    // 🔍 Búsqueda multivalor
    public List<Usuario> buscar(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return listarTodos();
        }
        return usuarioRepository.searchByKeyword(keyword);
    }
}
