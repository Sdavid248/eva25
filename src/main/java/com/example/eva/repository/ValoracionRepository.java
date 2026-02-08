package com.example.eva.repository;

import com.example.eva.model.Valoracion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ValoracionRepository extends JpaRepository<Valoracion, Long> {

    void deleteByUsuarioIdUser(Long idUser);
}
