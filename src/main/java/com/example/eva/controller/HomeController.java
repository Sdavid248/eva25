package com.example.eva.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.eva.model.Usuario;
import com.example.eva.repository.UsuarioRepository;

@Controller
public class HomeController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Página principal
    @GetMapping("/")
    public String inicio() {
        return "index"; // templates/index.html
    }

    // Acerca de
    @GetMapping("/acercade")
    public String acercaDe() {
        return "acercade"; // templates/acercade.html
    }

    // Centros deportivos
    @GetMapping("/centrosdeportivos")
    public String centrosDeportivos() {
        return "centrosdeportivos"; // templates/centrosdeportivos.html
    }

    // Información
    @GetMapping("/info")
    public String info() {
        return "info"; // templates/info.html
    }

    // Login
    @GetMapping("/login")
    public String login() {
        return "login"; // templates/login.html
    }

    // Formulario de registro
    @GetMapping("/registro")
    public String registroForm(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "registro"; // templates/registro.html
    }

    // Procesar registro
    @PostMapping("/registro")
    public String registrarUsuario(@ModelAttribute Usuario usuario, Model model) {
        try {
            usuarioRepository.save(usuario);
            model.addAttribute("mensaje", "✅ Usuario registrado con éxito");
        } catch (Exception e) {
            model.addAttribute("mensaje", "❌ Error al registrar el usuario: " + e.getMessage());
        }
        return "registro"; // Vuelve al formulario con mensaje
    }

    // Mapa
    @GetMapping("/mapa")
    public String mapa() {
        return "mapa"; // templates/mapa.html
    }
}
