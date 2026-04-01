package com.example.eva.service;

import com.example.eva.model.CentroDeportivo;
import com.example.eva.model.Rol;
import com.example.eva.model.Usuario;
import com.example.eva.model.UsuarioRol;
import com.example.eva.repository.CentroDeportivoRepository;
import com.example.eva.repository.RolRepository;
import com.example.eva.repository.UsuarioRepository;
import com.example.eva.repository.UsuarioRolRepository;

import org.springframework.stereotype.Service;

@Service
public class AdminCentroService {

    private final UsuarioRepository usuarioRepo;
    private final CentroDeportivoRepository centroRepo;
    private final RolRepository rolRepo;
    private final UsuarioRolRepository usuarioRolRepo;

    public AdminCentroService(UsuarioRepository usuarioRepo,
            CentroDeportivoRepository centroRepo,
            RolRepository rolRepo,
            UsuarioRolRepository usuarioRolRepo) {
        this.usuarioRepo = usuarioRepo;
        this.centroRepo = centroRepo;
        this.rolRepo = rolRepo;
        this.usuarioRolRepo = usuarioRolRepo;
    }

    public void asignarAdminCentro(Long userId, Integer centroId) {

        Usuario usuario = usuarioRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        CentroDeportivo centro = centroRepo.findById(centroId)
                .orElseThrow(() -> new RuntimeException("Centro no encontrado"));

        // 🔥 1. asignar admin al centro
        centro.setAdmin(usuario);
        centroRepo.save(centro);

        // 🔥 2. buscar rol
        Rol rolAdminCentro = rolRepo.findByNombre("ROLE_ADMIN_CENTRO")
                .orElseThrow(() -> new RuntimeException("Rol no existe"));

        // 🔥 3. validar si ya tiene el rol
        boolean yaExiste = usuarioRolRepo
                .findByUsuarioAndRol(usuario, rolAdminCentro)
                .isPresent();

        // 🔥 4. asignar rol si no lo tiene
        if (!yaExiste) {
            UsuarioRol usuarioRol = new UsuarioRol();
            usuarioRol.setUsuario(usuario);
            usuarioRol.setRol(rolAdminCentro);

            usuarioRolRepo.save(usuarioRol);
        }
        if (centro.getAdmin() != null) {
            throw new RuntimeException("Este centro ya tiene administrador");
        }
    }
}
