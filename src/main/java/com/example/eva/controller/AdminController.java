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

    @GetMapping("/admin")
    public String adminRoot() {
        return "redirect:/admin/dashboard";
    }
@GetMapping("/admin/dashboard")
@PreAuthorize("hasRole('ADMIN')")
public String panelAdmin(Model model) {

    // 📊 métricas
    model.addAttribute("totalUsuarios", usuarioRepo.count());
    model.addAttribute("totalCentros", centroRepo.count());

    model.addAttribute("usuariosPorEstado",
            usuarioRepo.countGroupedByEstado());

    model.addAttribute("usuariosPorRol",
            usuarioRepo.countUsuariosByRol());

    // 🔥 ESTO ES LO QUE TE FALTABA
    model.addAttribute("centros", centroRepo.findAll());
    model.addAttribute("usuarios", usuarioRepo.findAll());
    model.addAttribute("esAdmin", true);

    return "admin_dashboard";
}
}