package com.example.eva.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String index() {
        return "index"; // templates/index.html
    }

    @GetMapping("/acercade")
    public String acercade() {
        return "acercade"; // templates/acercade.html
    }

    @GetMapping("/info")
    public String info() {
        return "info"; // templates/info.html
    }

    @GetMapping("/mapa")
    public String mapa() {
        return "mapa"; // templates/mapa.html
    }

    @GetMapping("/centrosdeportivos")
    public String centrosDeportivos() {
        return "centrosdeportivos"; // templates/centrosdeportivos.html
    }
}
