package com.example.eva.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

    // =======================================================
    // LISTAR USUARIOS
    // =======================================================
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

        // Saber si es admin para la vista
        boolean isAdmin = SecurityContextHolder.getContext()
                .getAuthentication()
                .getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        model.addAttribute("isAdmin", isAdmin);

        Page<Usuario> usuariosPage;

        // =======================================================
        // CORRECCIÓN: antes llamabas a un método que NO existía
        // usuariosPage = usuarioService.buscarPaginado(keyword, pageable);
        // Ahora reuso buscarConFiltrosPaginado() que sí existe
        // =======================================================

        if (keyword != null && !keyword.isEmpty()) {

            usuariosPage = usuarioService.buscarConFiltrosPaginado(
                    keyword, // nombre
                    keyword, // correo
                    estado,
                    documento,
                    telefono,
                    direccion,
                    pageable
            );

            model.addAttribute("usuarios", usuariosPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", usuariosPage.getTotalPages());

        } else {

            usuariosPage = usuarioService.buscarConFiltrosPaginado(
                    nombre, correo, estado, documento, telefono, direccion, pageable);

            model.addAttribute("usuarios", usuariosPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", usuariosPage.getTotalPages());
        }

        // Mantener filtros
        model.addAttribute("keyword", keyword);
        model.addAttribute("nombre", nombre);
        model.addAttribute("correo", correo);
        model.addAttribute("estado", estado);
        model.addAttribute("documento", documento);
        model.addAttribute("telefono", telefono);
        model.addAttribute("direccion", direccion);

        return "lista"; // 👉 ARCHIVO EXACTO: src/main/resources/templates/lista.html
    }

    // =======================================================
    // AUXILIAR: verificar admin
    // =======================================================
    private boolean esAdmin() {
        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    // =======================================================
    // NUEVO USUARIO
    // =======================================================
    @GetMapping("/nuevo")
    @PreAuthorize("hasRole('ADMIN')")
    public String nuevoUsuarioForm(Model model) {

        if (!esAdmin()) return "redirect:/usuario?error=permiso";

        model.addAttribute("usuario", new Usuario());
        model.addAttribute("roles", rolRepository.findAll());
        return "/form";
    }

    // =======================================================
    // GUARDAR CREACIÓN
    // =======================================================
    @PostMapping("/guardar")
    @PreAuthorize("hasRole('ADMIN')")
    public String guardarUsuario(@ModelAttribute Usuario usuario,
                                 @RequestParam(required = false) Long rolId) {

        if (!esAdmin()) return "redirect:/usuario?error=permiso";

        Usuario u = usuarioService.guardar(usuario);

        if (rolId != null) {
            Rol rol = rolRepository.findById(rolId)
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

            u.getUsuarioRoles().clear();

            UsuarioRol ur = new UsuarioRol();
            ur.setUsuario(u);
            ur.setRol(rol);

            u.getUsuarioRoles().add(ur);

            usuarioService.guardar(u);
        }

        return "redirect:/usuario";
    }

    // =======================================================
    // EDITAR
    // =======================================================
    @GetMapping("/editar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String editarUsuario(@PathVariable Long id, Model model) {

        if (!esAdmin()) return "redirect:/usuario?error=permiso";

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

    // =======================================================
    // ELIMINAR
    // =======================================================
    @GetMapping("/eliminar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String eliminarUsuario(@PathVariable Long id) {

        if (!esAdmin()) return "redirect:/usuario?error=permiso";

        usuarioService.eliminar(id);
        return "redirect:/usuario";
    }

    // =======================================================
    // GUARDAR EDICIÓN
    // =======================================================
    @PostMapping("/save")
    @PreAuthorize("hasRole('ADMIN')")
    public String guardarDesdeFormulario(@ModelAttribute Usuario usuario,
                                         @RequestParam(required = false) Long rolId) {

        if (!esAdmin()) return "redirect:/usuario?error=permiso";

        Usuario u = usuarioService.guardar(usuario);

        if (rolId != null) {
            Rol rol = rolRepository.findById(rolId)
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

            u.getUsuarioRoles().clear();

            UsuarioRol ur = new UsuarioRol();
            ur.setUsuario(u);
            ur.setRol(rol);

            u.getUsuarioRoles().add(ur);

            usuarioService.guardar(u);
        }

        return "redirect:/usuario";
    }
}
