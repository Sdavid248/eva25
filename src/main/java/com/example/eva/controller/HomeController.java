package com.example.eva.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String index() {
        return "index"; 
    }

    @GetMapping("/acercade")
    public String acercade() {
        return "acercade"; 
    }

    @GetMapping("/info")
    public String info() {
        return "info"; 
    }


}
