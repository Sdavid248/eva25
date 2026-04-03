package com.example.eva.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

// IMPORTS
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SeguridadTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void deberiaRedirigirSinAutenticacion() throws Exception {
        mockMvc.perform(get("/centrosdeportivos"))
                .andExpect(status().is3xxRedirection());
    }
}