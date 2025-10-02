package com.example.eva.controller;

import com.example.eva.model.Usuario;
import com.example.eva.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/usuario")
public class UsuarioCrudController {

    private final UsuarioService usuarioService;

    @Autowired
    public UsuarioCrudController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // 🔹 Listar usuarios
    @GetMapping
    public String listarUsuarios(Model model, @RequestParam(required = false) String keyword) {
        model.addAttribute("usuarios", usuarioService.buscar(keyword));
        model.addAttribute("keyword", keyword);
        return "lista"; // apunta a templates/lista.html
    }

    // 🔹 Mostrar formulario nuevo
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "form"; // apunta a templates/form.html
    }

    // 🔹 Guardar usuario
    @PostMapping("/save")
    public String guardarUsuario(@ModelAttribute Usuario usuario) {
        usuarioService.guardar(usuario);
        return "redirect:/usuario"; // redirige a la lista
    }

    // 🔹 Editar usuario
    @GetMapping("/editar/{id}")
    public String editarUsuario(@PathVariable Long id, Model model) {
        Usuario usuario = usuarioService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con id: " + id));
        model.addAttribute("usuario", usuario);
        return "form"; // usa el mismo form.html
    }

    // 🔹 Eliminar usuario
    @GetMapping("/eliminar/{id}")
    public String eliminarUsuario(@PathVariable Long id) {
        usuarioService.eliminar(id);
        return "redirect:/usuario"; // redirige a la lista
    }
}
