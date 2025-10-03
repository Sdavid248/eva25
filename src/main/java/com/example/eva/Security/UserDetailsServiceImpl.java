package com.example.eva.Security;

import com.example.eva.model.Usuario;
import com.example.eva.model.UsuarioRol;
import com.example.eva.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        // Usamos fetchRoles SIEMPRE
        Usuario usuario = usuarioRepository.findByCorreoFetchRoles(correo)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + correo));

        Set<GrantedAuthority> authorities = new HashSet<>();
        for (UsuarioRol ur : usuario.getUsuarioRoles()) {
            String rolName = ur.getRol().getNombre();
            authorities.add(new SimpleGrantedAuthority("ROLE_" + rolName.toUpperCase()));
        }

        // 🔎 Debug: ver roles cargados
        System.out.println("Usuario autenticado: " + correo + " con roles: " + authorities);

        return new User(usuario.getCorreo(), usuario.getContrasena(), authorities);
    }
}
