package com.example.eva.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

// IMPORTS
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class RegistroFallidoTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser
    void deberiaFallarRegistro() throws Exception {
        mockMvc.perform(post("/registro"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?registroExitoso=false"));
    }
}