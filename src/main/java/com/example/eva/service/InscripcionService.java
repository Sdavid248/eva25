package com.example.eva.service;

import com.example.eva.model.CentroDeportivo;
import com.example.eva.model.Inscripcion;
import com.example.eva.model.Usuario;
import com.example.eva.repository.CentroDeportivoRepository;
import com.example.eva.repository.InscripcionRepository;
import com.example.eva.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InscripcionService {

    @Autowired
    private InscripcionRepository inscripcionRepo;

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Autowired
    private CentroDeportivoRepository centroRepo;

    // 🔥 AHORA USA QUERY (NO STREAM)
    public boolean estaInscrito(Long idUser, Integer rutCentro) {
        return inscripcionRepo.existsByUsuarioIdUserAndCentroDeportivoRut(idUser, rutCentro);
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

        // 🔥 RELACIÓN CORRECTA
        ins.setCentroDeportivo(centro);

        inscripcionRepo.save(ins);
    }

    public void inscribirMasivo(Integer rutCentro, List<Long> usuarios) {
        for (Long idUser : usuarios) {
            if (!estaInscrito(idUser, rutCentro)) {
                inscribir(idUser, rutCentro);
            }
        }
    }

    public List<Inscripcion> obtenerInscripcionesPorUsuario(Long idUser) {
        return inscripcionRepo.findByUsuarioIdUser(idUser);
    }

    // 🔥 CANCELAR
    public void cancelar(Long numero, Long idUser) {

        Inscripcion ins = inscripcionRepo.findById(numero)
                .orElseThrow(() -> new RuntimeException("Inscripción no encontrada"));

        if (!ins.getUsuario().getIdUser().equals(idUser)) {
            throw new RuntimeException("No tienes permiso para cancelar esta inscripción");
        }

        inscripcionRepo.delete(ins);
    }

    // 🔥 ADMIN
    public List<Inscripcion> obtenerPorCentro(Integer rutCentro) {
        return inscripcionRepo.findAll()
                .stream()
                .filter(i -> i.getCentroDeportivo().getRut().equals(rutCentro))
                .toList();
    }
}