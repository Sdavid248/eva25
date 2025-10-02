package com.example.eva.repository;

import com.example.eva.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long>, JpaSpecificationExecutor<Usuario> {

    // 🔑 Buscar usuario por correo
    Optional<Usuario> findByCorreo(String correo);

    // 🔑 Buscar usuario + roles (para autenticación / seguridad)
    @Query("SELECT u FROM Usuario u " +
           "LEFT JOIN FETCH u.usuarioRoles ur " +
           "LEFT JOIN FETCH ur.rol " +
           "WHERE u.correo = :correo")
    Optional<Usuario> findByCorreoFetchRoles(@Param("correo") String correo);

    // 🔍 Búsqueda rápida por keyword (nombre, correo, estado, documento)
    @Query("SELECT u FROM Usuario u " +
           "WHERE LOWER(u.nombre) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "   OR LOWER(u.correo) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "   OR LOWER(u.estado) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "   OR CAST(u.documento AS string) LIKE CONCAT('%', :keyword, '%')")
    List<Usuario> searchByKeyword(@Param("keyword") String keyword);

}
