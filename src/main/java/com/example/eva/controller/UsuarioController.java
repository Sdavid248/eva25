package com.example.eva.controller;

import com.example.eva.model.Usuario;
import com.example.eva.service.UsuarioService;
import com.example.eva.util.PdfGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Controller
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PdfGenerator pdfGenerator;

    // 📄 Exportar PDF con búsqueda simple
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/pdf")
    public void exportarPDF(HttpServletResponse response,
            @RequestParam(required = false) String keyword) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=usuarios.pdf");

        List<Usuario> usuarios = usuarioService.buscar(keyword);
        pdfGenerator.generarUsuariosPDF(usuarios, response.getOutputStream());
    }

    // 📄 Exportar PDF con filtros múltiples (7 filtros)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/pdf-filtros")
    public void exportarPDFFiltros(HttpServletResponse response,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String correo,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String documento,
            @RequestParam(required = false) String telefono,
            @RequestParam(required = false) String direccion) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=usuarios_filtros.pdf");

        // 🔹 Ahora pasamos los 6 filtros al servicio
        List<Usuario> usuarios = usuarioService.buscarConFiltros(nombre, correo, estado, documento, telefono, direccion);
        pdfGenerator.generarUsuariosPDF(usuarios, response.getOutputStream());
    }
}
