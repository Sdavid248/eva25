package com.example.eva.Security;

import com.example.eva.model.Usuario;
import com.example.eva.model.UsuarioRol;
import com.example.eva.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {

        Usuario usuario = usuarioRepository.findByCorreoFetchRoles(correo)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + correo));

        Set<GrantedAuthority> authorities = new HashSet<>();

        for (UsuarioRol ur : usuario.getUsuarioRoles()) {

            String rolName = ur.getRol().getNombre();

            if (rolName == null) continue;

            rolName = rolName.toUpperCase().trim();

            // 🔥 EVITA DOBLE ROLE_
            if (!rolName.startsWith("ROLE_")) {
                rolName = "ROLE_" + rolName;
            }

            authorities.add(new SimpleGrantedAuthority(rolName));
        }

        System.out.println("DEBUG LOGIN -> " + correo + " ROLES: " + authorities);

        return new User(
                usuario.getCorreo(),
                usuario.getContrasena(),
                authorities
        );
    }
}