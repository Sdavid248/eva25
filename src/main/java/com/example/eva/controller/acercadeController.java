package com.example.eva.controller;
import org.springframework.web.bind.annotation.GetMapping;

public class acercadeController {
    
    @GetMapping("/")
    public String inicio() {
        return "index"; // busca index.html en templates
    }

    @GetMapping("/acerca_de")
    public String acercaDe() {
        return "acerca_de"; // busca acerca_de.html en templates
    }

    @GetMapping("/centrosdeportivos")
    public String centrosDeportivos() {
        return "centrosdeportivos"; 
    }

    @GetMapping("/info")
    public String info() {
        return "info"; 
    }
}
