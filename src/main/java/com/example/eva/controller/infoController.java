package com.example.eva.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class infoController {

    @GetMapping("/info")
    public String mostrarInfo() {
        return "info"; // busca info.html en templates
    }
}
