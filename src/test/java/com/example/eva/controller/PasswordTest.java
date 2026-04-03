package com.example.eva.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

// IMPORT
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class PasswordTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void deberiaEncriptarPassword() {
        String raw = "1234";
        String encoded = passwordEncoder.encode(raw);

        assertNotEquals(raw, encoded);
    }
}