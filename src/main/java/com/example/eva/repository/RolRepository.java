package com.example.eva.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.eva.model.Rol;

public interface RolRepository extends JpaRepository<Rol, Long> {
    Optional<Rol> findByNombre(String nombre);

    
    List<Rol> findAllByOrderByNombreAsc();

   
    List<Rol> findByNombreContainingIgnoreCase(String nombre);
}
