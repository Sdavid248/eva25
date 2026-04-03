package com.example.eva.repository;

import com.example.eva.model.Inscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface InscripcionRepository extends JpaRepository<Inscripcion, Long> {

    void deleteByUsuarioIdUser(Long idUser);

    boolean existsByUsuarioIdUserAndRut(Long idUser, Integer rut);

    List<Inscripcion> findByUsuarioIdUser(Long idUser);

    @Query("SELECT i.usuario.nombre, COUNT(i) FROM Inscripcion i GROUP BY i.usuario.nombre")
    List<Object[]> countInscripcionesPorUsuario();

    @Query("SELECT c.nombre, u.estado, COUNT(u) " +
            "FROM Inscripcion i " +
            "JOIN i.usuario u " +
            "JOIN i.centroDeportivo c " +
            "GROUP BY c.nombre, u.estado " +
            "ORDER BY c.nombre")
    List<Object[]> countUsuariosEstadoPorCentro();
}