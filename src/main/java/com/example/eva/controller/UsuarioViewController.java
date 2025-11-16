package com.example.eva.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.eva.model.Usuario;
import com.example.eva.service.UsuarioService;

@Controller
@RequestMapping("/usuarios")
public class UsuarioViewController {

    @Autowired
    private UsuarioService usuarioService;

    // 📋 Listar usuarios con filtros + paginación
    @GetMapping
    public String listarUsuarios(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,

            // Filtros (los 6 que exige tu UsuarioService)
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String correo,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String documento,
            @RequestParam(required = false) String telefono,
            @RequestParam(required = false) String direccion,

            Model model) {

        Pageable pageable = PageRequest.of(page, size);

        // ✔ Llamada 100% compatible con UsuarioService
        Page<Usuario> usuarios = usuarioService.buscarConFiltrosPaginado(
                nombre, correo, estado, documento, telefono, direccion, pageable);

        model.addAttribute("usuarios", usuarios.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", usuarios.getTotalPages());

        // ✔ Mantener filtros en pantalla
        model.addAttribute("nombre", nombre);
        model.addAttribute("correo", correo);
        model.addAttribute("estado", estado);
        model.addAttribute("documento", documento);
        model.addAttribute("telefono", telefono);
        model.addAttribute("direccion", direccion);

        return "view"; // usa templates/view.html
    }

    // ➕ Crear usuario
    @GetMapping("/nuevo")
    public String nuevoUsuario(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "form"; // templates/form.html
    }

    // ✏️ Editar usuario
    @GetMapping("/editar/{id}")
    public String editarUsuario(@PathVariable Long id, Model model) {
        Usuario usuario = usuarioService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con id: " + id));

        model.addAttribute("usuario", usuario);
        return "form"; // templates/form.html
    }

    // 💾 Guardar usuario
    @PostMapping("/save")
    public String guardarUsuario(@ModelAttribute Usuario usuario) {
        usuarioService.guardar(usuario);
        return "redirect:/usuarios";
    }

    // 🗑️ Eliminar usuario
    @GetMapping("/eliminar/{id}")
    public String eliminarUsuario(@PathVariable Long id) {
        usuarioService.eliminar(id);
        return "redirect:/usuarios";
    }
}
