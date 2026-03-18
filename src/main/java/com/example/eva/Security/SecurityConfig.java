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

                // 🔓 Páginas públicas
                .requestMatchers("/", "/index", "/acercade", "/info",
                        "/registro", "/login",
                        "/css/**", "/js/**", "/images/**")
                .permitAll()

                // 🔥 SOLO ADMIN puede crear centros deportivos
                .requestMatchers(HttpMethod.POST, "/centrosdeportivos/nuevo")
                .hasRole("ADMIN")

                // Notificaciones
                .requestMatchers(HttpMethod.GET, "/notificaciones", "/notificaciones/**")
                .authenticated()

                .requestMatchers(HttpMethod.POST, "/notificaciones/enviar")
                .hasRole("ADMIN")

                // Usuarios
                .requestMatchers("/usuarios", "/usuarios/", "/usuarios/page/**")
                .authenticated()

                .requestMatchers(
                        "/usuarios/nuevo",
                        "/usuarios/editar/**",
                        "/usuarios/eliminar/**"
                ).hasRole("ADMIN")

                // Correo
                .requestMatchers("/correo/**").hasRole("ADMIN")

                // PDF
                .requestMatchers("/pdf", "/pdf-filtros").hasRole("ADMIN")

                // Todo lo demás requiere login
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .usernameParameter("correo")
                .passwordParameter("contrasena")
                .defaultSuccessUrl("/", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            )
            .exceptionHandling(ex -> ex
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.sendRedirect("/");
                })
            );

        return http.build();
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