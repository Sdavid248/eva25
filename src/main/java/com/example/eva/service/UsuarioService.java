package com.example.eva.service;

import com.example.eva.model.Usuario;
import com.example.eva.repository.UsuarioRepository;
import com.example.eva.repository.InscripcionRepository;
import com.example.eva.repository.ValoracionRepository;
import com.example.eva.repository.UsuarioRolRepository;
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

    // 🔍 Buscar usuarios (usando tu query searchByKeyword)
    public List<Usuario> buscar(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return usuarioRepository.findAll();
        }
        return usuarioRepository.searchByKeyword(keyword);
    }

    // 💾 Guardar usuario (encripta password si aplica)
    public Usuario guardar(Usuario usuario) {
        if (usuario.getContrasena() != null && !usuario.getContrasena().isBlank()) {
            usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));
        }
        return usuarioRepository.save(usuario);
    }

    // 🔎 Buscar por ID
    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    // 🗑️ Eliminar usuario y relaciones
    public void eliminar(Long id) {
        inscripcionRepository.deleteByUsuarioIdUser(id);
        valoracionRepository.deleteByUsuarioIdUser(id);
        usuarioRolRepository.deleteByUsuarioIdUser(id);
        usuarioRepository.deleteById(id);
    }
}
