package com.example.eva.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class acercadeController {

    @GetMapping("/acercade")
    public String mostrarAcercaDe() {
        // Busca el archivo acercade.html en src/main/resources/templates
        return "acercade";
    }
}
