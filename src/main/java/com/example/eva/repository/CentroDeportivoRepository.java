package com.example.eva.repository;

import com.example.eva.model.CentroDeportivo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CentroDeportivoRepository extends JpaRepository<CentroDeportivo, Integer> {

    boolean existsByNombreAndDireccion(String nombre, String direccion);

    // 🔥 BUSQUEDA POR NOMBRE
    List<CentroDeportivo> findByNombreContainingIgnoreCase(String nombre);
}