package com.example.eva.Security;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.eva.model.Rol;
import com.example.eva.model.Usuario;
import com.example.eva.model.UsuarioRol;
import com.example.eva.repository.RolRepository;
import com.example.eva.repository.UsuarioRepository;
import com.example.eva.repository.UsuarioRolRepository;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(UsuarioRepository usuarioRepository,
                               RolRepository rolRepository,
                               UsuarioRolRepository usuarioRolRepository,
                               PasswordEncoder passwordEncoder) {
        return args -> {
            // Crear roles si no existen
            Rol rolUser = rolRepository.findByNombre("USER").orElseGet(() -> {
                Rol r = new Rol();
                r.setNombre("USER");
                return rolRepository.save(r);
            });

            Rol rolAdmin = rolRepository.findByNombre("ADMIN").orElseGet(() -> {
                Rol r = new Rol();
                r.setNombre("ADMIN");
                return rolRepository.save(r);
            });

            // Crear usuario normal
            Usuario user = usuarioRepository.findByCorreo("user@demo.com").orElseGet(() -> {
                Usuario u = new Usuario();
                u.setDocumento("1040323456"); // ⚡ requerido
                u.setNombre("Usuario Demo");
                u.setDireccion("Calle 123");
                u.setTelefono("3001112233");
                u.setCorreo("user@demo.com");
                u.setEstado("ACTIVO");
                u.setContrasena(passwordEncoder.encode("User123"));
                return usuarioRepository.save(u);
            });

            // Crear usuario admin
            Usuario admin = usuarioRepository.findByCorreo("admin@demo.com").orElseGet(() -> {
                Usuario u = new Usuario();
                u.setDocumento("1021392980"); // ⚡ requerido
                u.setNombre("Administrador");
                u.setDireccion("Av Siempre Viva 742");
                u.setTelefono("3009998877");
                u.setCorreo("admin@demo.com");
                u.setEstado("ACTIVO");
                u.setContrasena(passwordEncoder.encode("Admin123"));
                return usuarioRepository.save(u);
            });

            // Asociar roles a usuarios
            if (usuarioRolRepository.findByUsuarioAndRol(user, rolUser).isEmpty()) {
                UsuarioRol ur = new UsuarioRol();
                ur.setUsuario(user);
                ur.setRol(rolUser);
                usuarioRolRepository.save(ur);
            }

            if (usuarioRolRepository.findByUsuarioAndRol(admin, rolAdmin).isEmpty()) {
                UsuarioRol ur = new UsuarioRol();
                ur.setUsuario(admin);
                ur.setRol(rolAdmin);
                usuarioRolRepository.save(ur);
            }
        };
    }
}
