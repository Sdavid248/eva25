package com.example.eva.controller;

import com.example.eva.model.CentroDeportivo;
import com.example.eva.model.Usuario;
import com.example.eva.service.InscripcionService;
import com.example.eva.service.UsuarioService;
import com.example.eva.repository.CentroDeportivoRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/inscripciones")
public class InscripcionController {

    @Autowired
    private InscripcionService inscripcionService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private CentroDeportivoRepository centroRepo;

    // FORMULARIO
    @GetMapping("/form/{rut}")
    public String formulario(@PathVariable Integer rut, Model model) {

        CentroDeportivo centro = centroRepo.findById(rut)
                .orElseThrow(() -> new RuntimeException("Centro no encontrado"));

        model.addAttribute("centro", centro);

        return "inscripcion_form";
    }

    // GUARDAR INSCRIPCIÓN
    @PostMapping("/guardar")
    public String guardar(@RequestParam Integer rut, RedirectAttributes ra) {

        Usuario usuario = usuarioService.obtenerUsuarioLogueado();

        if (usuario == null) {
            return "redirect:/login";
        }

        try {
            inscripcionService.inscribir(usuario.getIdUser(), rut);
            ra.addFlashAttribute("ok", "Inscripción realizada correctamente");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error: " + e.getMessage());
        }

        return "redirect:/centrosdeportivos";
    }
}