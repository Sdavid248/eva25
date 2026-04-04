package com.example.eva.service;

import com.example.eva.model.Valoracion;
import com.example.eva.model.Usuario;
import com.example.eva.repository.ValoracionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

@Service
public class ValoracionService {

    private final ValoracionRepository repo;

    public ValoracionService(ValoracionRepository repo) {
        this.repo = repo;
    }

    // 🔥 GUARDAR VALORACIÓN (PRO: evita duplicados)
    public void guardar(Usuario usuario, Integer rut, int estrellas, String comentario) {

        System.out.println("🔥 GUARDANDO VALORACION...");
        System.out.println("Usuario: " + usuario.getIdUser());
        System.out.println("Centro: " + rut);

        // 🔥 VALIDACIÓN PRO
        if (repo.existsByUsuarioIdUserAndRut(usuario.getIdUser(), rut)) {
            throw new RuntimeException("Ya valoraste este centro");
        }

        Valoracion v = new Valoracion();
        v.setUsuario(usuario);
        v.setRut(rut);
        v.setValoracion(estrellas);
        v.setCometario(comentario);
        v.setFhValoracion(LocalDateTime.now().toString());

        repo.save(v);

        System.out.println("✅ GUARDADO EN BD");
    }

    // 🔥 OBTENER POR CENTRO
    public List<Valoracion> obtenerPorCentro(Integer rut) {
        return repo.findByRut(rut);
    }

    // 🔥 PROMEDIO
    public double promedio(Integer rut) {
        List<Valoracion> lista = repo.findByRut(rut);

        if (lista.isEmpty()) return 0;

        return lista.stream()
                .mapToInt(Valoracion::getValoracion)
                .average()
                .orElse(0);
    }
    public List<Map<String, Object>> topCentros() {

    List<Object[]> lista = repo.topCentros();
    List<Map<String, Object>> response = new ArrayList<>();

    for (Object[] obj : lista) {

        Map<String, Object> data = new HashMap<>();
        data.put("rut", obj[0]);
        data.put("promedio", obj[1]);

        response.add(data);
    }

    return response;
}
}