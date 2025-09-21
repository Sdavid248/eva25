package com.example.eva.service;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.eva.model.Usuario;
import com.example.eva.repository.UsuarioRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {  

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByCorreo(correo)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + correo));

        return new User(
            usuario.getCorreo(),
            usuario.getContrasena(),
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
}
