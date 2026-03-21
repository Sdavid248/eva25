package com.example.eva.repository;

import com.example.eva.model.Usuario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UsuarioRepositoryTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    @DisplayName("findByCorreo debe retornar usuario cuando existe")
    void findByCorreo_debeRetornarUsuario() {
        Usuario usuario = new Usuario();
        usuario.setNombre("Ana");
        usuario.setCorreo("ana@test.com");
        usuario.setContrasena("123");
        usuario.setEstado("ACTIVO");
        usuario.setTelefono("3001234567");
        usuario.setDireccion("Calle 1");

        usuarioRepository.save(usuario);

        Optional<Usuario> resultado = usuarioRepository.findByCorreo("ana@test.com");

        assertTrue(resultado.isPresent());
        assertEquals("Ana", resultado.get().getNombre());
    }

    @Test
    @DisplayName("existsByCorreo debe retornar true cuando el correo existe")
    void existsByCorreo_debeRetornarTrue() {
        Usuario usuario = new Usuario();
        usuario.setNombre("Luis");
        usuario.setCorreo("luis@test.com");
        usuario.setContrasena("123");
        usuario.setEstado("ACTIVO");
        usuario.setTelefono("3000000000");
        usuario.setDireccion("Calle 2");

        usuarioRepository.save(usuario);

        boolean existe = usuarioRepository.existsByCorreo("luis@test.com");

        assertTrue(existe);
    }

    @Test
    @DisplayName("searchByKeyword debe buscar por nombre")
    void searchByKeyword_debeBuscarPorNombre() {
        Usuario usuario = new Usuario();
        usuario.setNombre("Carlos Ramírez");
        usuario.setCorreo("carlos@test.com");
        usuario.setContrasena("123");
        usuario.setEstado("ACTIVO");
        usuario.setTelefono("3111111111");
        usuario.setDireccion("Bogotá");

        usuarioRepository.save(usuario);

        List<Usuario> resultados = usuarioRepository.searchByKeyword("carlos");

        assertFalse(resultados.isEmpty());
        assertEquals("Carlos Ramírez", resultados.get(0).getNombre());
    }

    @Test
    @DisplayName("searchByKeyword debe buscar por correo")
    void searchByKeyword_debeBuscarPorCorreo() {
        Usuario usuario = new Usuario();
        usuario.setNombre("María");
        usuario.setCorreo("maria@test.com");
        usuario.setContrasena("123");
        usuario.setEstado("INACTIVO");
        usuario.setTelefono("3222222222");
        usuario.setDireccion("Medellín");

        usuarioRepository.save(usuario);

        List<Usuario> resultados = usuarioRepository.searchByKeyword("maria@test.com");

        assertFalse(resultados.isEmpty());
        assertEquals("María", resultados.get(0).getNombre());
    }

    @Test
    @DisplayName("countByEstado debe contar correctamente")
    void countByEstado_debeContarCorrectamente() {
        Usuario u1 = new Usuario();
        u1.setNombre("A");
        u1.setCorreo("a@test.com");
        u1.setContrasena("123");
        u1.setEstado("ACTIVO");
        u1.setTelefono("1");
        u1.setDireccion("Dir1");

        Usuario u2 = new Usuario();
        u2.setNombre("B");
        u2.setCorreo("b@test.com");
        u2.setContrasena("123");
        u2.setEstado("ACTIVO");
        u2.setTelefono("2");
        u2.setDireccion("Dir2");

        Usuario u3 = new Usuario();
        u3.setNombre("C");
        u3.setCorreo("c@test.com");
        u3.setContrasena("123");
        u3.setEstado("INACTIVO");
        u3.setTelefono("3");
        u3.setDireccion("Dir3");

        usuarioRepository.saveAll(List.of(u1, u2, u3));

        long activos = usuarioRepository.countByEstado("ACTIVO");
        long inactivos = usuarioRepository.countByEstado("INACTIVO");

        assertEquals(2, activos);
        assertEquals(1, inactivos);
    }

    @Test
    @DisplayName("obtenerCorreos debe retornar lista de correos")
    void obtenerCorreos_debeRetornarCorreos() {
        Usuario u1 = new Usuario();
        u1.setNombre("A");
        u1.setCorreo("a@test.com");
        u1.setContrasena("123");
        u1.setEstado("ACTIVO");
        u1.setTelefono("1");
        u1.setDireccion("Dir1");

        Usuario u2 = new Usuario();
        u2.setNombre("B");
        u2.setCorreo("b@test.com");
        u2.setContrasena("123");
        u2.setEstado("ACTIVO");
        u2.setTelefono("2");
        u2.setDireccion("Dir2");

        usuarioRepository.saveAll(List.of(u1, u2));

        List<String> correos = usuarioRepository.obtenerCorreos();

        assertEquals(2, correos.size());
        assertTrue(correos.contains("a@test.com"));
        assertTrue(correos.contains("b@test.com"));
    }

    @Test
    @DisplayName("totalUsuarios debe retornar el total")
    void totalUsuarios_debeRetornarTotal() {
        Usuario u1 = new Usuario();
        u1.setNombre("A");
        u1.setCorreo("a1@test.com");
        u1.setContrasena("123");
        u1.setEstado("ACTIVO");
        u1.setTelefono("1");
        u1.setDireccion("Dir1");

        Usuario u2 = new Usuario();
        u2.setNombre("B");
        u2.setCorreo("b1@test.com");
        u2.setContrasena("123");
        u2.setEstado("ACTIVO");
        u2.setTelefono("2");
        u2.setDireccion("Dir2");

        usuarioRepository.saveAll(List.of(u1, u2));

        Long total = usuarioRepository.totalUsuarios();

        assertEquals(2L, total);
    }
}