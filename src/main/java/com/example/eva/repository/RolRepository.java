package com.example.eva.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.eva.model.Rol;

public interface RolRepository extends JpaRepository<Rol, Long> {
    Optional<Rol> findByNombre(String nombre);

    // 🔹 Listar todos los roles en orden alfabético (útil para el <select> en el formulario)
    List<Rol> findAllByOrderByNombreAsc();

    // 🔹 Buscar roles cuyo nombre contenga texto (útil para futuros filtros si deseas)
    List<Rol> findByNombreContainingIgnoreCase(String nombre);
}
