package com.example.eva.controller;

import com.example.eva.model.Usuario;
import com.example.eva.model.CentroDeportivo;
import com.example.eva.service.UsuarioService;
import com.example.eva.service.AdminCentroService;
import com.example.eva.repository.CentroDeportivoRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private AdminCentroService adminCentroService;

    @Autowired
    private CentroDeportivoRepository centroRepo;

    // =========================
    // 🔎 LISTAR USUARIOS
    // =========================
    @GetMapping("/usuarios")
    public String listarUsuarios(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String correo,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String documento,
            @RequestParam(required = false) String telefono,
            @RequestParam(required = false) String direccion,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model
    ) {

        Pageable pageable = PageRequest.of(page, size);

        Page<Usuario> paginaUsuarios = usuarioService.buscarConFiltrosPaginado(
                nombre, correo, estado, documento, telefono, direccion, pageable
        );

        model.addAttribute("usuarios", paginaUsuarios.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", paginaUsuarios.getTotalPages());
        model.addAttribute("totalItems", paginaUsuarios.getTotalElements());

        model.addAttribute("nombre", nombre);
        model.addAttribute("correo", correo);
        model.addAttribute("estado", estado);
        model.addAttribute("documento", documento);
        model.addAttribute("telefono", telefono);
        model.addAttribute("direccion", direccion);

        return "lista";
    }

    // =========================
    // 🔥 FORMULARIO CREAR CENTRO
    // =========================
    @GetMapping("/usuario/crear-centro")
    public String formCrearCentro(Model model) {

        model.addAttribute("centro", new CentroDeportivo());

        return "crear_centro";
    }

    // =========================
    // 🔥 CREAR CENTRO Y SER ADMIN
    // =========================
    @PostMapping("/usuario/crear-centro")
    public String guardarCentro(@ModelAttribute CentroDeportivo centro,
                               Authentication authentication) {

        Usuario usuario = (Usuario) authentication.getPrincipal();

        // 🔥 asignar admin automáticamente
        centro.setAdmin(usuario);

        // 🔥 guardar centro
        CentroDeportivo centroGuardado = centroRepo.save(centro);

        // 🔥 asignar rol ADMIN_CENTRO
        adminCentroService.asignarAdminCentro(
                usuario.getIdUser(),
                centroGuardado.getRut()
        );

        return "redirect:/centro-admin/dashboard";
    }
}