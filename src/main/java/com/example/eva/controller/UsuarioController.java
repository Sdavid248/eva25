package com.example.eva.controller;

import com.example.eva.model.Usuario;
import com.example.eva.service.UsuarioService;
import com.example.eva.util.PdfGenerator;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.List;

@Controller
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PdfGenerator pdfGenerator;

    @GetMapping("/pdf")
    public void exportarPDF(HttpServletResponse response,
                            @RequestParam(required = false) String keyword) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=usuarios.pdf");

        List<Usuario> usuarios;
        if (keyword != null && !keyword.isEmpty()) {
            usuarios = usuarioService.buscar(keyword);
        } else {
            usuarios = usuarioService.listarTodos();
        }

        // ⚡ Usamos el bean inyectado
        pdfGenerator.generarUsuariosPDF(usuarios, response.getOutputStream());
    }
}
