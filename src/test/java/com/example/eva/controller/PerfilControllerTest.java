package com.example.eva.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PerfilController.class)
@AutoConfigureMockMvc(addFilters = false)
class PerfilControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /Perfilusuario debe retornar la vista Perfilusuario con los datos por defecto")
    void mostrarPerfil_debeRetornarVistaYModelo() throws Exception {
        mockMvc.perform(get("/Perfilusuario"))
                .andExpect(status().isOk())
                .andExpect(view().name("Perfilusuario"))
                .andExpect(model().attribute("nombre", ""))
                .andExpect(model().attribute("correo", ""))
                .andExpect(model().attribute("ubicacion", ""))
                .andExpect(model().attribute("edad", ""))
                .andExpect(model().attribute("ocupacion", ""))
                .andExpect(model().attribute("intereses", ""))
                .andExpect(model().attribute("foto", "https://via.placeholder.com/150"));
    }
}