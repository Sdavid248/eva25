package com.example.eva.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.eva.model.UsuarioRol;
import com.example.eva.model.Usuario;
import com.example.eva.model.Rol;
import java.util.Optional;

public interface UsuarioRolRepository extends JpaRepository<UsuarioRol, Long> {
    // Ya existente
    Optional<UsuarioRol> findByUsuarioAndRol(Usuario usuario, Rol rol);

    // 🔹 Necesario para poder eliminar en cascada desde UsuarioService
    void deleteByUsuarioIdUser(Long idUser);
}