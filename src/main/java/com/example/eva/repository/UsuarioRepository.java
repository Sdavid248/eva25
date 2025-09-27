package com.example.eva.repository;

import com.example.eva.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByCorreo(String correo);

    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.usuarioRoles ur LEFT JOIN FETCH ur.rol WHERE u.correo = :correo")
    Optional<Usuario> findByCorreoFetchRoles(@Param("correo") String correo);

    // 🔍 búsqueda multivalor
    @Query("SELECT u FROM Usuario u " +
           "WHERE LOWER(u.nombre) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "   OR LOWER(u.correo) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "   OR LOWER(u.estado) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "   OR CAST(u.documento AS string) LIKE CONCAT('%', :keyword, '%')")
    List<Usuario> searchByKeyword(@Param("keyword") String keyword);
}
