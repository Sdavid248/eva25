package com.example.eva.repository;

import com.example.eva.model.Valoracion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ValoracionRepository extends JpaRepository<Valoracion, Long> {

    List<Valoracion> findByRut(Integer rut);

    List<Valoracion> findByUsuarioIdUser(Long idUser);

    void deleteByUsuarioIdUser(Long idUser);

    // 🔥 NUEVO (para después evitar duplicados)
    boolean existsByUsuarioIdUserAndRut(Long idUser, Integer rut);

    @Query("""
    SELECT v.rut, AVG(v.valoracion)
    FROM Valoracion v
    GROUP BY v.rut
    ORDER BY AVG(v.valoracion) DESC
""")
    List<Object[]> topCentros();
}
