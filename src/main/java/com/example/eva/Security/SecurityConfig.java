package com.example.eva.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;

    public SecurityConfig(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authenticationProvider(authenticationProvider())
            .authorizeHttpRequests(auth -> auth

                // 🔓 PUBLICO
                .requestMatchers("/centrosdeportivos/cercanos").authenticated()
                .requestMatchers("/centrosdeportivos/buscar").authenticated()
                .requestMatchers("/", "/index", "/acercade", "/info",
                        "/registro", "/login",
                        "/css/**", "/js/**", "/images/**")
                .permitAll()

                // 👤 PERFIL
                .requestMatchers("/perfil/**").authenticated()
                .requestMatchers("/usuario/crear-centro/**").authenticated()

                // 🟣 ADMIN DE CENTRO
                .requestMatchers("/usuario/ser-admin/**").authenticated()
                .requestMatchers("/centro-admin/**").hasRole("ADMIN_CENTRO")
                .requestMatchers(HttpMethod.POST, "/centrosdeportivos/guardar").hasRole("ADMIN")

                // 🔴 ADMIN TOTAL
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/correo/**").hasRole("ADMIN")
                .requestMatchers("/pdf/**").hasRole("ADMIN")
                .requestMatchers("/notificaciones/enviar").hasRole("ADMIN")

                // 👥 USUARIOS
                .requestMatchers("/usuarios/**").hasRole("ADMIN")

                // 🏟️ CENTROS
                .requestMatchers(HttpMethod.POST, "/centrosdeportivos/nuevo").hasRole("ADMIN")
                .requestMatchers("/centrosdeportivos/eliminar/**").hasRole("ADMIN")
                .requestMatchers("/centrosdeportivos/editar/**").hasRole("ADMIN")

                // 👤 ACCIONES USUARIO
                .requestMatchers(HttpMethod.POST, "/centrosdeportivos/inscribirme/**").authenticated()
                .requestMatchers("/centrosdeportivos/usuario/**").authenticated()

                // 🔥 IMPORTANTE (VALORACIONES)
                .requestMatchers(HttpMethod.POST, "/centrosdeportivos/valorar/**").authenticated()

                // 🔒 TODO LO DEMÁS
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .usernameParameter("correo")
                .passwordParameter("contrasena")
                .successHandler(customAuthenticationSuccessHandler())
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .exceptionHandling(ex -> ex
                .accessDeniedHandler((request, response, e) -> {
                    response.sendRedirect("/");
                })
            );

        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        return (request, response, authentication) -> {

            boolean isAdmin = false;
            boolean isUser = false;
            boolean isAdminCentro = false;

            for (GrantedAuthority auth : authentication.getAuthorities()) {
                if (auth.getAuthority().equals("ROLE_ADMIN")) isAdmin = true;
                if (auth.getAuthority().equals("ROLE_USER")) isUser = true;
                if (auth.getAuthority().equals("ROLE_ADMIN_CENTRO")) isAdminCentro = true;
            }

            if (isAdmin) {
                response.sendRedirect("/admin/dashboard");
            } else if (isAdminCentro) {
                response.sendRedirect("/centro-admin/dashboard");
            } else if (isUser) {
                response.sendRedirect("/perfil");
            } else {
                response.sendRedirect("/");
            }
        };
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(userDetailsService);
        auth.setPasswordEncoder(passwordEncoder());
        return auth;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}