package com.example.eva.service;

import com.example.eva.model.CentroDeportivo;
import com.example.eva.model.Usuario;
import com.example.eva.repository.CentroDeportivoRepository;
import com.example.eva.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class InscripcionArchivoService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CentroDeportivoRepository centroRepository;

    @Autowired
    private InscripcionService inscripcionService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Map<String, Integer> procesarCSVTexto(String csv, Integer rutCentro) {

        Map<String, Integer> resultado = new HashMap<>();
        resultado.put("creados", 0);
        resultado.put("inscritos", 0);
        resultado.put("repetidos", 0);

        CentroDeportivo centro = centroRepository.findById(rutCentro)
                .orElseThrow(() -> new RuntimeException("Centro no encontrado"));

        for (String linea : csv.split("\\r?\\n")) {

            if (linea.isBlank()) continue;

            String[] d = linea.split(";");
            if (d.length < 4) continue; 

            String nombre = d[0].trim();
            String correo = d[1].trim();
            String telefono = d[2].trim();
            String direccion = d[3].trim();

            Usuario usuario = usuarioRepository.findByCorreo(correo).orElse(null);

            if (usuario == null) {
                usuario = new Usuario();
                usuario.setDocumento(String.valueOf(
                        (int) (Math.random() * 90000000) + 10000000
                ));
                usuario.setNombre(nombre);
                usuario.setDireccion(direccion);
                usuario.setTelefono(telefono);
                usuario.setCorreo(correo);
                usuario.setEstado("activo");
                usuario.setContrasena(passwordEncoder.encode("Temporal123"));

                usuarioRepository.save(usuario);
                resultado.put("creados", resultado.get("creados") + 1);
            }

            if (inscripcionService.estaInscrito(usuario.getIdUser(), centro.getRut())) {
                resultado.put("repetidos", resultado.get("repetidos") + 1);
                continue;
            }

            inscripcionService.inscribir(usuario.getIdUser(), centro.getRut());
            resultado.put("inscritos", resultado.get("inscritos") + 1);
        }

        return resultado;
    }
}
