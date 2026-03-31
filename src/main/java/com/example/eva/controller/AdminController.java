package com.example.eva.controller;

import com.example.eva.model.CentroDeportivo;
import com.example.eva.model.Usuario;
import com.example.eva.repository.CentroDeportivoRepository;
import com.example.eva.repository.UsuarioRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class AdminController {

    private final CentroDeportivoRepository centroRepo;
    private final UsuarioRepository usuarioRepo;

    public AdminController(CentroDeportivoRepository centroRepo, UsuarioRepository usuarioRepo) {
        this.centroRepo = centroRepo;
        this.usuarioRepo = usuarioRepo;
    }

    @GetMapping("/admin/dashboard")  // ✅ Cambiado a /admin/dashboard
    @PreAuthorize("hasRole('ADMIN')")
    public String panelAdmin(Model model) {
        List<CentroDeportivo> centros = centroRepo.findAll();
        List<Usuario> usuarios = usuarioRepo.findAll();

        model.addAttribute("centros", centros);
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("esAdmin", true);

        model.addAttribute("creados", 0);
        model.addAttribute("inscritos", 0);
        model.addAttribute("repetidos", 0);

        return "admin_dashboard"; // debe coincidir con el nombre del HTML
    }
}