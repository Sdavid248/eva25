package com.example.eva.repository;

import com.example.eva.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long>, JpaSpecificationExecutor<Usuario> {

    long countByEstado(String estado);

    @Query("SELECT u.estado, COUNT(u) FROM Usuario u GROUP BY u.estado")
    List<Object[]> countGroupedByEstado();

    Optional<Usuario> findByCorreo(String correo);

    @Query("SELECT u FROM Usuario u "
            + "LEFT JOIN FETCH u.usuarioRoles ur "
            + "LEFT JOIN FETCH ur.rol "
            + "WHERE u.correo = :correo")
    Optional<Usuario> findByCorreoFetchRoles(@Param("correo") String correo);

    boolean existsByCorreo(String correo);

    @Query("SELECT u FROM Usuario u "
            + "WHERE LOWER(u.nombre) LIKE LOWER(CONCAT('%', :keyword, '%')) "
            + " OR LOWER(u.correo) LIKE LOWER(CONCAT('%', :keyword, '%')) "
            + " OR LOWER(u.estado) LIKE LOWER(CONCAT('%', :keyword, '%')) "
            + " OR CAST(u.documento AS string) LIKE CONCAT('%', :keyword, '%') "
            + " OR LOWER(u.telefono) LIKE LOWER(CONCAT('%', :keyword, '%')) "
            + " OR LOWER(u.direccion) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Usuario> searchByKeyword(@Param("keyword") String keyword);

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

    @Query("SELECT u.correo FROM Usuario u")
    List<String> obtenerCorreos();


    @Query("SELECT r.nombre, COUNT(u) " +
            "FROM Usuario u " +
            "JOIN u.usuarioRoles ur " +
            "JOIN ur.rol r " +
            "GROUP BY r.nombre")
    List<Object[]> countUsuariosByRol();


    @Query("SELECT new com.example.eva.dto.EstadoCantidadDTO(u.estado, COUNT(u)) " +
            "FROM Usuario u GROUP BY u.estado")
    List<Object[]> cantidadUsuariosPorEstadoDTO();


    @Query("SELECT COUNT(u) FROM Usuario u")
    Long totalUsuarios();





    @Query("SELECT u FROM Usuario u " +
            "WHERE LOWER(u.nombre) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "   OR LOWER(u.correo) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Usuario> buscarPorNombreOCorreo(@Param("keyword") String keyword);


    @Query("SELECT u FROM Usuario u WHERE " +
            "(:nombre IS NULL OR LOWER(u.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) AND " +
            "(:correo IS NULL OR LOWER(u.correo) LIKE LOWER(CONCAT('%', :correo, '%'))) AND " +
            "(:estado IS NULL OR LOWER(u.estado) LIKE LOWER(CONCAT('%', :estado, '%'))) AND " +
            "(:documento IS NULL OR CAST(u.documento AS string) LIKE CONCAT('%', :documento, '%')) AND " +
            "(:telefono IS NULL OR LOWER(u.telefono) LIKE LOWER(CONCAT('%', :telefono, '%'))) AND " +
            "(:direccion IS NULL OR LOWER(u.direccion) LIKE LOWER(CONCAT('%', :direccion, '%')))")
    List<Usuario> buscarPorFiltros(
            @Param("nombre") String nombre,
            @Param("correo") String correo,
            @Param("estado") String estado,
            @Param("documento") String documento,
            @Param("telefono") String telefono,
            @Param("direccion") String direccion
    );
}
