package com.example.eva.controller;

import com.example.eva.model.Usuario;
import com.example.eva.repository.UsuarioRepository;
import com.example.eva.util.PdfGenerator;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class PdfController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PdfGenerator pdfGenerator;


    // ==================================
    //     PDF PARA BÚSQUEDA SIMPLE
    // ==================================
    @GetMapping("/pdf")
    public void generarPdfBusqueda(
            @RequestParam(required = false) String keyword,
            HttpServletResponse response) throws Exception {

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=usuarios.pdf");

        List<Usuario> usuarios;

        if (keyword == null || keyword.isEmpty()) {
            usuarios = usuarioRepository.findAll();
        } else {
            usuarios = usuarioRepository.buscarPorNombreOCorreo(keyword);
        }

        pdfGenerator.generarUsuariosPDF(usuarios, response.getOutputStream());
    }


    // ==================================
    //      PDF PARA FILTROS AVANZADOS
    // ==================================
    @GetMapping("/pdf-filtros")
    public void generarPdfFiltros(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String correo,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String documento,
            @RequestParam(required = false) String telefono,
            @RequestParam(required = false) String direccion,
            HttpServletResponse response) throws Exception {

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=usuarios_filtro.pdf");

        List<Usuario> usuarios = usuarioRepository.buscarPorFiltros(
                nombre,
                correo,
                estado,
                documento,
                telefono,
                direccion
        );

        pdfGenerator.generarUsuariosPDF(usuarios, response.getOutputStream());
    }
}
