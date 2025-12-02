package com.example.eva.controller;

import com.example.eva.service.CorreoService;
import com.example.eva.util.LoggerEva;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/correo")
public class CorreoController {

    @Autowired
    private CorreoService correoService;

    private final LoggerEva logger = LoggerEva.getInstancia();

    @GetMapping("/masivo")
    public String mostrarFormulario() {
        return "correo_masivo";
    }

    /**
     * Envía correos. Mantiene el mismo comportamiento: el método espera y devuelve la lista de errores.
     * Si quieres envío en segundo plano, usa correoService.enviarCorreoMasivoUnoPorUnoAsync(...) o el batch async.
     */
    @PostMapping("/enviar")
    public String enviar(
            @RequestParam("destinatarios") String destinatarios,
            @RequestParam("asunto") String asunto,
            @RequestParam("mensajeHtml") String mensajeHtml,
            Model model) {

        try {
            logger.info("Solicitud de envío masivo recibida.");

            if (destinatarios == null || destinatarios.isBlank()) {
                model.addAttribute("destinatariosCount", 0);
                model.addAttribute("errores", List.of());
                model.addAttribute("mensaje", "No hay destinatarios.");
                return "correo_resultado";
            }

            // separar por comas, ; o saltos de línea
            List<String> lista = Arrays.stream(destinatarios.split("[,;\\n]"))
                    .map(String::trim)
                    .filter(s -> !s.isBlank())
                    .collect(Collectors.toList());

            // Llamada SÍNCRONA (mantiene el comportamiento)
            String htmlFinal = correoService.generarCorreoHTML(asunto, mensajeHtml, "Usuario EVA");

List<String> errores = correoService.enviarCorreoMasivoUnoPorUno(lista, asunto, htmlFinal);


            model.addAttribute("destinatariosCount", lista.size());
            model.addAttribute("errores", errores);
            model.addAttribute("mensaje", "Envío procesado. " + (errores.isEmpty() ? "Sin errores." : (errores.size() + " fallidos.")));

            return "correo_resultado";
        } catch (Exception e) {
            logger.error("Error en controlador enviar: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("destinatariosCount", 0);
            model.addAttribute("errores", List.of(e.getMessage()));
            model.addAttribute("mensaje", "Error al procesar el envío.");
            return "correo_resultado";
        }
    }
}
