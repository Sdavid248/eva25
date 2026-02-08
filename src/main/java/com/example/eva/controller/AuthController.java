package com.example.eva.controller;

import com.example.eva.model.Usuario;
import com.example.eva.model.Rol;
import com.example.eva.model.UsuarioRol;
import com.example.eva.repository.UsuarioRepository;
import com.example.eva.repository.RolRepository;
import com.example.eva.repository.UsuarioRolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.HashSet;

@Controller
public class AuthController {
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private RolRepository rolRepository;
    @Autowired
    private UsuarioRolRepository usuarioRolRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String login() {
        return "login"; 
    }

    @GetMapping("/registro")
    public String mostrarRegistro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "registro"; // templates/registro.html
    }

    @PostMapping("/registro")
    public String registrarUsuario(@ModelAttribute Usuario usuario, Model model) {
        try {
       
            usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));
            Usuario usuarioGuardado = usuarioRepository.save(usuario);

   
            Rol rolUser = rolRepository.findByNombre("USER")
                    .orElseThrow(() -> new RuntimeException("Rol USER no encontrado"));

            UsuarioRol ur = new UsuarioRol();
            ur.setUsuario(usuarioGuardado);
            ur.setRol(rolUser);
            usuarioRolRepository.save(ur);

            if (usuarioGuardado.getUsuarioRoles() == null) {
                usuarioGuardado.setUsuarioRoles(new HashSet<>());
            }
            usuarioGuardado.getUsuarioRoles().add(ur);

            usuarioRepository.save(usuarioGuardado);

            model.addAttribute("mensaje", "✅ Usuario registrado con éxito. Ahora puedes iniciar sesión.");
            return "login"; 
        } catch (Exception e) {
            model.addAttribute("mensaje", " Error: " + e.getMessage());
            return "registro";
        }
    }
}
