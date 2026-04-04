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

    // 🔥 Redirige /admin → dashboard
    @GetMapping("/admin")
    public String redirigirAdmin() {
        return "redirect:/admin/dashboard";
    }

    // 🔐 PANEL ADMIN
    @GetMapping("/admin/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public String panelAdmin(Model model) {

        // 📊 MÉTRICAS
        model.addAttribute("totalUsuarios", usuarioRepo.count());
        model.addAttribute("totalCentros", centroRepo.count());

        model.addAttribute("usuariosPorEstado",
                usuarioRepo.countGroupedByEstado());

        model.addAttribute("usuariosPorRol",
                usuarioRepo.countUsuariosByRol());

        // 📋 LISTAS
        List<CentroDeportivo> centros = centroRepo.findAll();
        List<Usuario> usuarios = usuarioRepo.findAll();

        model.addAttribute("centros", centros);
        model.addAttribute("usuarios", usuarios);

        // 🔑 FLAG
        model.addAttribute("esAdmin", true);

        // 🔥 (opcional si lo usas en la vista)
        model.addAttribute("creados", 0);
        model.addAttribute("inscritos", 0);
        model.addAttribute("repetidos", 0);

        return "admin_dashboard";
    }
}