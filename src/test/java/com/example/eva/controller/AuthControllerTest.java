package com.example.eva.controller;

import com.example.eva.model.Rol;
import com.example.eva.model.Usuario;
import com.example.eva.model.UsuarioRol;
import com.example.eva.repository.RolRepository;
import com.example.eva.repository.UsuarioRepository;
import com.example.eva.repository.UsuarioRolRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @MockBean
    private RolRepository rolRepository;

    @MockBean
    private UsuarioRolRepository usuarioRolRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("GET /login debe retornar vista login")
    void login_debeRetornarVistaLogin() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    @DisplayName("GET /registro debe retornar vista registro y agregar usuario al modelo")
    void mostrarRegistro_debeRetornarVistaRegistro() throws Exception {
        mockMvc.perform(get("/registro"))
                .andExpect(status().isOk())
                .andExpect(view().name("registro"))
                .andExpect(model().attributeExists("usuario"));
    }

    @Test
    @DisplayName("POST /registro debe registrar usuario correctamente")
    void registrarUsuario_debeGuardarUsuarioYRetornarLogin() throws Exception {
        Usuario usuarioGuardado = new Usuario();
        usuarioGuardado.setIdUser(1L);
        usuarioGuardado.setNombre("Juan");
        usuarioGuardado.setCorreo("juan@test.com");
        usuarioGuardado.setContrasena("clave-encriptada");
        usuarioGuardado.setUsuarioRoles(new HashSet<>());

        Rol rolUser = new Rol();
        rolUser.setId(1L);
        rolUser.setNombre("USER");

        when(passwordEncoder.encode("123456")).thenReturn("clave-encriptada");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioGuardado);
        when(rolRepository.findByNombre("USER")).thenReturn(Optional.of(rolUser));
        when(usuarioRolRepository.save(any(UsuarioRol.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(post("/registro")
                        .param("nombre", "Juan")
                        .param("correo", "juan@test.com")
                        .param("contrasena", "123456"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attributeExists("mensaje"));

        verify(passwordEncoder).encode("123456");
        verify(usuarioRepository, atLeastOnce()).save(any(Usuario.class));
        verify(rolRepository).findByNombre("USER");
        verify(usuarioRolRepository).save(any(UsuarioRol.class));
    }

    @Test
    @DisplayName("POST /registro debe retornar registro si ocurre error")
    void registrarUsuario_siOcurreError_debeRetornarRegistro() throws Exception {
        when(passwordEncoder.encode("123456")).thenReturn("clave-encriptada");
        when(usuarioRepository.save(any(Usuario.class)))
                .thenThrow(new RuntimeException("Error al guardar usuario"));

        mockMvc.perform(post("/registro")
                        .param("nombre", "Juan")
                        .param("correo", "juan@test.com")
                        .param("contrasena", "123456"))
                .andExpect(status().isOk())
                .andExpect(view().name("registro"))
                .andExpect(model().attributeExists("mensaje"));

        verify(passwordEncoder).encode("123456");
        verify(usuarioRepository).save(any(Usuario.class));
        verify(rolRepository, never()).findByNombre(anyString());
    }

    @Test
    @DisplayName("POST /registro debe retornar registro si no existe rol USER")
    void registrarUsuario_siNoExisteRolUser_debeRetornarRegistro() throws Exception {
        Usuario usuarioGuardado = new Usuario();
        usuarioGuardado.setIdUser(1L);
        usuarioGuardado.setNombre("Juan");
        usuarioGuardado.setCorreo("juan@test.com");
        usuarioGuardado.setContrasena("clave-encriptada");

        when(passwordEncoder.encode("123456")).thenReturn("clave-encriptada");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioGuardado);
        when(rolRepository.findByNombre("USER")).thenReturn(Optional.empty());

        mockMvc.perform(post("/registro")
                        .param("nombre", "Juan")
                        .param("correo", "juan@test.com")
                        .param("contrasena", "123456"))
                .andExpect(status().isOk())
                .andExpect(view().name("registro"))
                .andExpect(model().attributeExists("mensaje"));

        verify(rolRepository).findByNombre("USER");
    }
}