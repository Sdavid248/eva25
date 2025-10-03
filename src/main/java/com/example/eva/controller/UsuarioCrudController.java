package com.example.eva.controller;

import com.example.eva.model.Usuario;
import com.example.eva.model.Rol;
import com.example.eva.model.UsuarioRol;
import com.example.eva.repository.RolRepository;
import com.example.eva.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize; // 🔹 Importante

import java.util.HashSet;
import java.util.Optional; // 🔹 agregado para trabajar con Optional

// 📑 imports adicionales para paginación
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Controller
@RequestMapping("/usuario")
public class UsuarioCrudController {

    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private RolRepository rolRepository;

    // ✅ Todos los usuarios autenticados pueden ver la lista
    // 🔍 Ahora con soporte de filtros y paginación
    @GetMapping
    public String listarUsuarios(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String correo,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String documento,
            @RequestParam(required = false) String telefono,   // ✅ nuevo filtro
            @RequestParam(required = false) String direccion,  // ✅ nuevo filtro
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Usuario> usuariosPage;

        // 🔍 Si viene un "keyword" => búsqueda rápida
        if (keyword != null && !keyword.isEmpty()) {
            model.addAttribute("usuarios", usuarioService.searchByKeyword(keyword));
            model.addAttribute("currentPage", 0);
            model.addAttribute("totalPages", 1);
        } else {
            // 🔍 Si no hay keyword => filtros avanzados con paginación
            usuariosPage = usuarioService.searchWithFilters(
                    nombre, correo, estado, documento, telefono, direccion, pageable);

            model.addAttribute("usuarios", usuariosPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", usuariosPage.getTotalPages());
        }

        // ✅ mantener valores de los filtros en el modelo
        model.addAttribute("nombre", nombre);
        model.addAttribute("correo", correo);
        model.addAttribute("estado", estado);
        model.addAttribute("documento", documento);
        model.addAttribute("telefono", telefono);
        model.addAttribute("direccion", direccion);
        model.addAttribute("keyword", keyword);

        return "/lista"; // ⚠️ aquí asegúrate de que tu archivo esté en templates/lista.html
    }

    // 🔒 Solo ADMIN puede crear
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/nuevo")
    public String nuevoUsuarioForm(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("roles", rolRepository.findAll()); // 🔹 lista de roles para el <select>
        return "/form"; // ⚠️ archivo en templates/form.html
    }

    // 🔒 Solo ADMIN puede guardar
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/guardar")
    public String guardarUsuario(@ModelAttribute Usuario usuario,
                                 @RequestParam(required = false) Long rolId) {
        Usuario u = usuarioService.guardar(usuario);

        if (rolId != null) {
            Rol rol = rolRepository.findById(rolId)
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

            if (u.getUsuarioRoles() == null) {
                u.setUsuarioRoles(new HashSet<>());
            }

            // 🔹 Se asegura de que solo haya UN rol por usuario
            u.getUsuarioRoles().clear();
            UsuarioRol ur = new UsuarioRol();
            ur.setUsuario(u);
            ur.setRol(rol);
            u.getUsuarioRoles().add(ur);

            usuarioService.guardar(u);
        }

        return "redirect:/usuario";
    }

    // 🔒 Solo ADMIN puede editar
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/editar/{id}")
    public String editarUsuario(@PathVariable Long id, Model model) {
        Usuario usuario = usuarioService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        model.addAttribute("usuario", usuario);
        model.addAttribute("roles", rolRepository.findAll()); // 🔹 lista de roles para el <select>

        // 🔹 obtener rol actual para preseleccionar en el formulario
        Optional<Rol> rolActual = usuario.getUsuarioRoles().stream()
                .map(UsuarioRol::getRol)
                .findFirst();
        rolActual.ifPresent(r -> model.addAttribute("rolId", r.getId()));

        return "/form";
    }

    // 🔒 Solo ADMIN puede eliminar
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/eliminar/{id}")
    public String eliminarUsuario(@PathVariable Long id) {
        usuarioService.eliminar(id);
        return "redirect:/usuario";
    }
}
