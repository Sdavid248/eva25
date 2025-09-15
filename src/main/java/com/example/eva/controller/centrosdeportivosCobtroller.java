package com.example.eva.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class centrosdeportivosCobtroller {

    @GetMapping("/centrosdeportivos")
    public String mostrarCentrosDeportivos() {
        return "centrosdeportivos"; // busca el archivo centrosdeportivos.html en templates
    }
}
