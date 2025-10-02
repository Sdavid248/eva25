package com.example.eva.repository;

import com.example.eva.model.Valoracion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ValoracionRepository extends JpaRepository<Valoracion, Long> {
    // Elimina todas las valoraciones asociadas a un usuario
    void deleteByUsuarioIdUser(Long idUser);
}
