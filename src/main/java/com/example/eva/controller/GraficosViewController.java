package com.example.eva.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GraficosViewController {

    @GetMapping("/graficos")
    public String mostrarPaginaGraficos() {
        return "graficos";  
    }
}
