package com.example.eva.service;

import com.example.eva.model.CentroDeportivo;
import com.example.eva.repository.CentroDeportivoRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class CentroDeportivoService {

    private final CentroDeportivoRepository repo;

    public CentroDeportivoService(CentroDeportivoRepository repo) {
        this.repo = repo;
    }

    // 🔥 HU-14: CENTROS CERCANOS
    public List<CentroDeportivo> obtenerCercanos(double lat, double lng) {

        return repo.findAll().stream()
                .filter(c -> c.getLat() != null && c.getLng() != null)
                .sorted(Comparator.comparingDouble(c ->
                        calcularDistancia(lat, lng, c.getLat(), c.getLng())))
                .limit(5)
                .toList();
    }

    // 🔥 HU-8: BUSCAR POR NOMBRE
    public List<CentroDeportivo> buscarPorNombre(String nombre) {

        return repo.findAll().stream()
                .filter(c -> c.getNombre() != null &&
                        c.getNombre().toLowerCase().contains(nombre.toLowerCase()))
                .toList();
    }

    // 🔥 MÉTODO PRIVADO (base)
    private double calcularDistancia(double lat1, double lon1, double lat2, double lon2) {

        final int R = 6371;

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }

    // 🔥 MÉTODO PÚBLICO (PARA EL CONTROLLER)
    public double calcularDistanciaPublica(double lat1, double lon1, Double lat2, Double lon2) {

        if (lat2 == null || lon2 == null) return Double.MAX_VALUE;

        return calcularDistancia(lat1, lon1, lat2, lon2);
    }
}