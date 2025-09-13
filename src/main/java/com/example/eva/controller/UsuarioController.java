package com.example.eva.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.eva.model.Usuario;
import com.example.eva.service.UsuarioService;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService UsuarioService) {
        this.usuarioService = UsuarioService;
    }



    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "registro"; // Debes tener usuarios/formulario.html
    }

    /**
     * @param usuario
     * @return
     */
    @PostMapping
    public String guardarUsuario(@ModelAttribute Usuario usuario) {
        usuarioService.guardar(usuario);
        return "registro";
    }

    /**
     * @param registro
     * @param model
     * @return
     */
    @GetMapping("/{registro}")
    public String mostrarFormulario(Model model) {

        model.addAttribute("usuario", new Usuario());
        return "registro"; // Debes tener usuarios/formulario.html
    }



}
