package com.example.eva.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.eva.model.Usuario;
import com.example.eva.service.UsuarioService;

@Controller
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // Mostrar formulario de registro
    @GetMapping("/registro")
    public String mostrarRegistro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "registro";
    }

    // Procesar registro
    @PostMapping("/registro")
    public String registrarUsuario(@ModelAttribute Usuario usuario, Model model) {
        usuarioService.guardar(usuario);
        model.addAttribute("mensaje", "Usuario registrado con éxito");
        model.addAttribute("usuario", new Usuario());
        return "registro";
    }

    // Página de inicio
    @GetMapping("/home")
    public String home() {
        return "home"; // home.html
    }

    // Página de login
    @GetMapping("/login")
    public String login() {
        return "login"; // login.html
    }
}
