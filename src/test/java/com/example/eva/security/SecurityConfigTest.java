package com.example.eva.security;

import com.example.eva.Security.UserDetailsServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @Test
    @DisplayName("GET /login debe ser público")
    void login_debeSerPublico() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /usuarios debe requerir autenticación")
    void usuarios_debeRequerirAutenticacion() throws Exception {
        mockMvc.perform(get("/usuarios"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("GET /usuarios/nuevo con USER debe ser denegado")
    void usuariosNuevo_conUser_debeSerDenegado() throws Exception {
        mockMvc.perform(get("/usuarios/nuevo")
                        .with(user("user@test.com").roles("USER")))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("GET /usuarios/nuevo con ADMIN debe poder acceder")
    void usuariosNuevo_conAdmin_debePermitirAcceso() throws Exception {
        mockMvc.perform(get("/usuarios/nuevo")
                        .with(user("admin@test.com").roles("ADMIN")))
                .andExpect(status().isOk());
    }
}