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

    // Inyección de dependencias por constructor
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // Mostrar formulario de registro
    @GetMapping("/registro")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "registro"; // archivo registro.html en templates
    }

    // Guardar usuario
    @PostMapping("/registro")
    public String guardarUsuario(@ModelAttribute Usuario usuario, Model model) {
        usuarioService.guardar(usuario);
        model.addAttribute("usuario", new Usuario()); // limpiar el formulario
        model.addAttribute("mensaje", "Usuario registrado con éxito");
        return "registro"; 
    }
}
