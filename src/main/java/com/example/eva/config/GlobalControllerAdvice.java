package com.example.eva.config;

import com.example.eva.model.Usuario;
import com.example.eva.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private UsuarioService usuarioService;

    @ModelAttribute("usuarioLogueado")
    public Usuario usuarioLogueado() {
        return usuarioService.obtenerUsuarioLogueado();
    }
}