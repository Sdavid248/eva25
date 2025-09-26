package com.example.eva.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.eva.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    Optional<Usuario> findByCorreo(String correo);

    // Este trae el usuario y sus roles en una sola consulta
    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.usuarioRoles ur LEFT JOIN FETCH ur.rol WHERE u.correo = :correo")
    Optional<Usuario> findByCorreoFetchRoles(@Param("correo") String correo);
}
