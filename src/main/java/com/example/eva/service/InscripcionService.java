package com.example.eva.service;

import com.example.eva.model.CentroDeportivo;
import com.example.eva.model.Inscripcion;
import com.example.eva.model.Usuario;
import com.example.eva.repository.CentroDeportivoRepository;
import com.example.eva.repository.InscripcionRepository;
import com.example.eva.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InscripcionService {

    @Autowired
    private InscripcionRepository inscripcionRepo;

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Autowired
    private CentroDeportivoRepository centroRepo;

    public boolean estaInscrito(Long idUser, Integer rutCentro) {
        return inscripcionRepo.existsByUsuarioIdUserAndRut(idUser, rutCentro);
    }

    public void inscribir(Long idUser, Integer rutCentro) {

        Usuario usuario = usuarioRepo.findById(idUser)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        CentroDeportivo centro = centroRepo.findById(rutCentro)
                .orElseThrow(() -> new RuntimeException("Centro no encontrado"));

        if (estaInscrito(idUser, rutCentro)) return;

        Inscripcion ins = new Inscripcion();
        ins.setUsuario(usuario);
        ins.setTelefono(usuario.getTelefono());
        ins.setCorreo(usuario.getCorreo());
        ins.setEstado("postulante");
        ins.setRut(centro.getRut());

        inscripcionRepo.save(ins);
    }

    public void inscribirMasivo(Integer rutCentro, java.util.List<Long> usuariosIds) {
        CentroDeportivo centro = centroRepo.findById(rutCentro)
                .orElseThrow(() -> new RuntimeException("Centro no encontrado"));

        for (Long usuarioId : usuariosIds) {
            inscribir(usuarioId, rutCentro);
        }
    }
}

