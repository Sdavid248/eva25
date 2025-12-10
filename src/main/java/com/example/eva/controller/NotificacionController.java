package com.example.eva.controller;

import com.example.eva.model.Usuario;
import com.example.eva.repository.UsuarioRepository;
import com.example.eva.service.CorreoService;
import com.example.eva.util.LoggerEva;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/notificaciones")
public class NotificacionController {

    private final CorreoService correoService;
    private final UsuarioRepository usuarioRepository;
    private final LoggerEva logger = LoggerEva.getInstancia();

    public NotificacionController(CorreoService correoService, UsuarioRepository usuarioRepository) {
        this.correoService = correoService;
        this.usuarioRepository = usuarioRepository;
    }

    
    @GetMapping
    public String mostrarFormulario(Model model) {
        List<Usuario> usuarios = usuarioRepository.findAll();
        model.addAttribute("usuarios", usuarios);
        return "notificaciones"; 
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/enviar")
    public String enviarNotificaciones(
            @RequestParam String titulo,
            @RequestParam String mensaje,
            Model model
    ) {

        logger.info("Solicitud de envío masivo recibida.");

        List<Usuario> usuarios = usuarioRepository.findAll();

        for (Usuario u : usuarios) {
            try {
                
                String html = correoService.generarCorreoHTML(titulo, mensaje, u.getNombre());
               
                correoService.enviarCorreoMasivoUnoPorUno(List.of(u.getCorreo()), titulo, html);
            } catch (Exception e) {
                logger.error("Fallo enviando a " + u.getCorreo() + " -> " + e.getMessage());
            }
        }

        model.addAttribute("exito", "Correos enviados correctamente!");
        return "notificaciones";
    }
}
