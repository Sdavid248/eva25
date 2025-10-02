package com.example.eva.repository;

import com.example.eva.model.Inscripcion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InscripcionRepository extends JpaRepository<Inscripcion, Long> {
    // Elimina todas las inscripciones asociadas a un usuario
    void deleteByUsuarioIdUser(Long idUser);
}
