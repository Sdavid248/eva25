package com.example.eva.controller;

import com.example.eva.model.Usuario;
import com.example.eva.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PerfilController {

    @GetMapping("/perfil")
    public String mostrarPerfil(Model model) {

        
        model.addAttribute("nombre", "Juan Pérez");
        model.addAttribute("correo", "juanperez@email.com");
        model.addAttribute("ubicacion", "Ciudad de México");
        model.addAttribute("edad", 28);
        model.addAttribute("ocupacion", "Desarrollador Web");
        model.addAttribute("intereses", "Programación, videojuegos, música");
        model.addAttribute("foto", "https://via.placeholder.com/150");

        return "perfil"; 
    }
}
