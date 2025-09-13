package com.example.eva.service;
import java.util.List;

import org.springframework.stereotype.Service;

import  com.example.eva.model.Usuario;
import com.example.eva.repository.UsuarioRepository;
@Service
public class UsuarioService {
    private final UsuarioRepository UsuarioRepository;

    public UsuarioService(UsuarioRepository suarioRepository) {
        this.UsuarioRepository = suarioRepository;
    }

    public List<Usuario> listarTodos() {
        return UsuarioRepository.findAll();
        // TODO Auto-generated method stub         
        
    }

    public void guardar(Usuario usuario) {
        UsuarioRepository.save(usuario);
    }
    public void actualizar(Usuario usuario) {
        UsuarioRepository.save(usuario);
    }
    public Usuario buscarid(Long id) {
        return UsuarioRepository.findById(id).orElse(null);
    }

}