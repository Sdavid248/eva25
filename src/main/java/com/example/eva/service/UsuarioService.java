package com.example.eva.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.eva.model.Usuario;
import com.example.eva.repository.UsuarioRepository;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public void guardar(Usuario usuario) {
        usuarioRepository.save(usuario);
    }

    public void actualizar(Usuario usuario) {
        usuarioRepository.save(usuario);
    }

    public Usuario buscarid(Long id) {
        return usuarioRepository.findById(id).orElse(null);
    }
}
