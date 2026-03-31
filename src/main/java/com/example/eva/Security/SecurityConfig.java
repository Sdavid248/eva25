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

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import com.example.eva.model.Rol;
import com.example.eva.Security.UserDetailsServiceImpl;
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
                // 🔓 PÚBLICO
                .requestMatchers("/", "/index", "/acercade", "/info",
                        "/registro", "/login",
                        "/css/**", "/js/**", "/images/**")
                .permitAll()
                // 👤 PERFIL
                .requestMatchers("/perfil/**").authenticated()
                // 🔥 ADMIN
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/centrosdeportivos/nuevo").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/notificaciones/enviar").hasRole("ADMIN")
                .requestMatchers("/usuarios/nuevo", "/usuarios/editar/**", "/usuarios/eliminar/**").hasRole("ADMIN")
                .requestMatchers("/correo/**").hasRole("ADMIN")
                .requestMatchers("/pdf", "/pdf-filtros").hasRole("ADMIN")
                .requestMatchers("/centrosdeportivos/nuevo").hasRole("ADMIN")
                // 🔔 NOTIFICACIONES
                .requestMatchers(HttpMethod.GET, "/notificaciones/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/centrosdeportivos/inscribirme/**").authenticated()
                // 👥 USUARIOS
                .requestMatchers("/usuarios", "/usuarios/", "/usuarios/page/**").authenticated()
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
        return new AuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request,
                                                HttpServletResponse response,
                                                Authentication authentication)
                                                throws IOException, ServletException {

                boolean isAdmin = false;
                boolean isUser = false;

                for (GrantedAuthority auth : authentication.getAuthorities()) {
                    if (auth.getAuthority().equals("ROLE_ADMIN")) isAdmin = true;
                    if (auth.getAuthority().equals("ROLE_USER")) isUser = true;
                }

                if (isAdmin) {
                    response.sendRedirect("/admin/dashboard");
                } else if (isUser) {
                    response.sendRedirect("/perfil");
                } else {
                    response.sendRedirect("/"); // fallback
                }
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