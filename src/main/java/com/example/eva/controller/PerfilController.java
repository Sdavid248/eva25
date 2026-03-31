package com.example.eva.controller;

import com.example.eva.model.Usuario;
import com.example.eva.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class PerfilController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/perfil")
    public String verPerfil(Model model) {

        Usuario usuario = usuarioService.obtenerUsuarioLogueado();

        if (usuario == null) {
            return "redirect:/login";
        }

        model.addAttribute("usuario", usuario);
        return "Perfilusuario";
    }

    @GetMapping("/perfil/editar")
    public String editarPerfil(Model model) {

        Usuario usuario = usuarioService.obtenerUsuarioLogueado();

        if (usuario == null) {
            return "redirect:/login";
        }

        model.addAttribute("usuario", usuario);
        return "editarperfil";
    }

    @PostMapping("/perfil/editar")
    public String guardarPerfil(@ModelAttribute Usuario usuarioForm) {

        Usuario usuario = usuarioService.obtenerUsuarioLogueado();

        if (usuario == null) {
            return "redirect:/login";
        }

        // 🔒 SOLO CAMPOS EDITABLES
        usuario.setNombre(usuarioForm.getNombre());
        usuario.setDireccion(usuarioForm.getDireccion());
        usuario.setTelefono(usuarioForm.getTelefono());
        usuario.setEdad(usuarioForm.getEdad());
        usuario.setOcupacion(usuarioForm.getOcupacion());
        usuario.setIntereses(usuarioForm.getIntereses());
        usuario.setFoto(usuarioForm.getFoto());

        usuarioService.guardar(usuario);

        return "redirect:/perfil";
    }
}