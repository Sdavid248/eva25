package com.example.eva.repository;

import com.example.eva.model.CentroDeportivo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CentroDeportivoRepository extends JpaRepository<CentroDeportivo, Integer> {
}
