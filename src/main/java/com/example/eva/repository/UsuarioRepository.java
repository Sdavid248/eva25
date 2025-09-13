package com.example.eva.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.eva.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // puedes agregar consultas personalizadas
}
