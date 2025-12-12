package com.example.eva.controller;

import com.example.eva.model.CentroDeportivo;
import com.example.eva.repository.CentroDeportivoRepository;
import com.example.eva.service.NominatimService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
public class CentroDeportivoController {

    @Autowired
    private CentroDeportivoRepository repo;

    @Autowired
    private NominatimService nominatim;

    @GetMapping("/centrosdeportivos")
    public String listarCentros(org.springframework.ui.Model model) {
        model.addAttribute("centros", repo.findAll());
        return "centrosdeportivos";
    }

    @GetMapping("/mapa")
    public String mapa(org.springframework.ui.Model model) {
        List<CentroDeportivo> centros = repo.findAll();
        List<Map<String, Object>> lista = new ArrayList<>();

        for (CentroDeportivo c : centros) {
            double[] coords = nominatim.obtenerCoordenadas(c.getDireccion());
            if (coords != null) {
                Map<String, Object> data = new HashMap<>();
                data.put("nombre", c.getNombre());
                data.put("direccion", c.getDireccion());
                data.put("lat", coords[0]);
                data.put("lng", coords[1]);
                lista.add(data);
            }
        }

        model.addAttribute("centros", lista);
        return "mapa";
    }

    @PostMapping("/centrosdeportivos/nuevo")
    @ResponseBody
    public ResponseEntity<?> guardarNuevoCentro(@RequestBody Map<String, String> body) {

        try {
            String nombre = body.get("nombre");
            String direccion = body.get("direccion");
            String telefono = body.get("telefono");
            String apertura = body.get("apertura");
            String cierre = body.get("cierre");

            double[] coords = nominatim.obtenerCoordenadas(direccion);
            if (coords == null) {
                return ResponseEntity.badRequest().body("ERROR: Dirección no encontrada");
            }

            CentroDeportivo cd = new CentroDeportivo();
            cd.setNombre(nombre);
            cd.setDireccion(direccion);
            cd.setTelefono(telefono);
            cd.setApertura(apertura);
            cd.setCierre(cierre);
            cd.setEstado("Activo");
            cd.setCapacidad(0);

            repo.save(cd);

            return ResponseEntity.ok("OK");

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("ERROR: " + e.getMessage());
        }
    }
}
