package com.example.eva.repository;

import com.example.eva.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

// 📑 imports extra para paginación
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UsuarioRepository extends JpaRepository<Usuario, Long>, JpaSpecificationExecutor<Usuario> {

    // 🔑 Buscar usuario por correo
    Optional<Usuario> findByCorreo(String correo);

    // 🔑 Buscar usuario + roles (para autenticación / seguridad)
    @Query("SELECT u FROM Usuario u "
            + "LEFT JOIN FETCH u.usuarioRoles ur "
            + "LEFT JOIN FETCH ur.rol "
            + "WHERE u.correo = :correo")
    Optional<Usuario> findByCorreoFetchRoles(@Param("correo") String correo);

    // 📑 NUEVO: validar si un correo ya existe (prevención duplicados)
    boolean existsByCorreo(String correo);

    // 🔍 Búsqueda rápida por keyword
    @Query("SELECT u FROM Usuario u "
            + "WHERE LOWER(u.nombre) LIKE LOWER(CONCAT('%', :keyword, '%')) "
            + "   OR LOWER(u.correo) LIKE LOWER(CONCAT('%', :keyword, '%')) "
            + "   OR LOWER(u.estado) LIKE LOWER(CONCAT('%', :keyword, '%')) "
            + "   OR CAST(u.documento AS string) LIKE CONCAT('%', :keyword, '%') "
            + "   OR LOWER(u.telefono) LIKE LOWER(CONCAT('%', :keyword, '%')) "
            + "   OR LOWER(u.direccion) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Usuario> searchByKeyword(@Param("keyword") String keyword);

    // 📑 NUEVO: búsqueda por filtros + paginación
    @Query("SELECT u FROM Usuario u "
            + "WHERE (:nombre IS NULL OR LOWER(u.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) "
            + "AND (:correo IS NULL OR LOWER(u.correo) LIKE LOWER(CONCAT('%', :correo, '%'))) "
            + "AND (:estado IS NULL OR LOWER(u.estado) LIKE LOWER(CONCAT('%', :estado, '%'))) "
            + "AND (:documento IS NULL OR CAST(u.documento AS string) LIKE CONCAT('%', :documento, '%')) "
            + "AND (:telefono IS NULL OR LOWER(u.telefono) LIKE LOWER(CONCAT('%', :telefono, '%'))) "
            + "AND (:direccion IS NULL OR LOWER(u.direccion) LIKE LOWER(CONCAT('%', :direccion, '%')))")
    Page<Usuario> searchWithFilters(@Param("nombre") String nombre,
            @Param("correo") String correo,
            @Param("estado") String estado,
            @Param("documento") String documento,
            @Param("telefono") String telefono,
            @Param("direccion") String direccion,
            Pageable pageable);
}
