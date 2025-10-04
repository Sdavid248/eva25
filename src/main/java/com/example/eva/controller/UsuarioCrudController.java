package com.example.eva.controller;

import java.util.HashSet;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping; // 🔹 Importante
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable; // 🔹 agregado para trabajar con Optional
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.eva.model.Rol;
import com.example.eva.model.Usuario;
import com.example.eva.model.UsuarioRol;
import com.example.eva.repository.RolRepository;
import com.example.eva.service.UsuarioService;

@Controller
@RequestMapping("/usuario")
public class UsuarioCrudController {

    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private RolRepository rolRepository;

    // ✅ Todos los usuarios autenticados pueden ver la lista
    @GetMapping
    public String listarUsuarios(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String correo,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String documento,
            @RequestParam(required = false) String telefono,
            @RequestParam(required = false) String direccion,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Usuario> usuariosPage;

        if (keyword != null && !keyword.isEmpty()) {
            model.addAttribute("usuarios", usuarioService.searchByKeyword(keyword));
            model.addAttribute("currentPage", 0);
            model.addAttribute("totalPages", 1);
        } else {
            usuariosPage = usuarioService.searchWithFilters(
                    nombre, correo, estado, documento, telefono, direccion, pageable);

            model.addAttribute("usuarios", usuariosPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", usuariosPage.getTotalPages());
        }

        model.addAttribute("nombre", nombre);
        model.addAttribute("correo", correo);
        model.addAttribute("estado", estado);
        model.addAttribute("documento", documento);
        model.addAttribute("telefono", telefono);
        model.addAttribute("direccion", direccion);
        model.addAttribute("keyword", keyword);

        return "/lista";
    }

    // 🔒 Solo ADMIN puede crear
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/nuevo")
    public String nuevoUsuarioForm(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("roles", rolRepository.findAll());
        return "/form";
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

            u.getUsuarioRoles().clear();
            UsuarioRol ur = new UsuarioRol();
            ur.setUsuario(u);
            ur.setRol(rol);
            u.getUsuarioRoles().add(ur);

            usuarioService.guardar(u);

            // ✅ Nuevo: asignar automáticamente permisos del rol
            usuarioService.asignarPermisosPorRol(u, rol);
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
        model.addAttribute("roles", rolRepository.findAll());

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

    // 🧩 NUEVO MÉTODO COMPATIBLE CON form.html (no borra nada existente)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/save")
    public String guardarDesdeFormulario(@ModelAttribute Usuario usuario,
                                         @RequestParam(required = false) Long rolId) {
        Usuario u = usuarioService.guardar(usuario);

        if (rolId != null) {
            Rol rol = rolRepository.findById(rolId)
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

            if (u.getUsuarioRoles() == null) {
                u.setUsuarioRoles(new HashSet<>());
            }

            u.getUsuarioRoles().clear();
            UsuarioRol ur = new UsuarioRol();
            ur.setUsuario(u);
            ur.setRol(rol);
            u.getUsuarioRoles().add(ur);

            usuarioService.guardar(u);

            // ✅ Nuevo: asignar automáticamente permisos del rol
            usuarioService.asignarPermisosPorRol(u, rol);
        }

        return "redirect:/usuario";
    }

}
