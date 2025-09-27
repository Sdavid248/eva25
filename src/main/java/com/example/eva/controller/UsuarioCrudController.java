package com.example.eva.controller;

import com.example.eva.model.Usuario;
import com.example.eva.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/usuarios")
public class UsuarioCrudController {

    private final UsuarioService usuarioService;

    public UsuarioCrudController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // 📌 Listar
    @GetMapping("/list")
    public String listar(Model model, @RequestParam(required = false) String keyword) {
        model.addAttribute("usuarios", usuarioService.buscar(keyword));
        model.addAttribute("keyword", keyword);
        return "usuarios/list"; // ✅ list.html
    }

    // 📌 Nuevo
    @GetMapping("/form")
    public String nuevo(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "usuarios/form"; // ✅ form.html
    }

    // 📌 Guardar (nuevo o editar)
    @PostMapping("/save")
    public String guardar(@ModelAttribute Usuario usuario) {
        usuarioService.guardar(usuario);
        return "redirect:/usuarios/list";
    }

    // 📌 Editar
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Usuario usuario = usuarioService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        model.addAttribute("usuario", usuario);
        return "usuarios/form"; // ✅ reutiliza form.html
    }

    // 📌 Eliminar
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        usuarioService.eliminar(id);
        return "redirect:/usuarios/list";
    }
}
