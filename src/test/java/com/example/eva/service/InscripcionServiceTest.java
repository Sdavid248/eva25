package com.example.eva.service;

import com.example.eva.model.CentroDeportivo;
import com.example.eva.model.Inscripcion;
import com.example.eva.model.Usuario;
import com.example.eva.repository.CentroDeportivoRepository;
import com.example.eva.repository.InscripcionRepository;
import com.example.eva.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@org.junit.jupiter.api.extension.ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class InscripcionServiceTest {

    @Mock
    private InscripcionRepository inscripcionRepo;

    @Mock
    private UsuarioRepository usuarioRepo;

    @Mock
    private CentroDeportivoRepository centroRepo;

    @InjectMocks
    private InscripcionService service;

    private Usuario usuario;
    private CentroDeportivo centro;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setIdUser(1L);
        usuario.setCorreo("test@test.com");
        usuario.setTelefono("123");

        centro = new CentroDeportivo();
        centro.setRut(100);
    }

    @Test
    @DisplayName("Debe retornar true si ya está inscrito")
    void estaInscrito_true() {
        when(inscripcionRepo.existsByUsuarioIdUserAndRut(1L, 100)).thenReturn(true);

        boolean resultado = service.estaInscrito(1L, 100);

        assertTrue(resultado);
    }

    @Test
    @DisplayName("Debe retornar false si no está inscrito")
    void estaInscrito_false() {
        when(inscripcionRepo.existsByUsuarioIdUserAndRut(1L, 100)).thenReturn(false);

        boolean resultado = service.estaInscrito(1L, 100);

        assertFalse(resultado);
    }

    @Test
    @DisplayName("Debe inscribir correctamente")
    void inscribir_ok() {

        when(usuarioRepo.findById(1L)).thenReturn(Optional.of(usuario));
        when(centroRepo.findById(100)).thenReturn(Optional.of(centro));
        when(inscripcionRepo.existsByUsuarioIdUserAndRut(1L, 100)).thenReturn(false);

        service.inscribir(1L, 100);

        ArgumentCaptor<Inscripcion> captor = ArgumentCaptor.forClass(Inscripcion.class);

        verify(inscripcionRepo).save(captor.capture());

        Inscripcion ins = captor.getValue();

        assertEquals("test@test.com", ins.getCorreo());
        assertEquals("123", ins.getTelefono());
        assertEquals("postulante", ins.getEstado());
        assertEquals(100, ins.getRut());
        assertEquals(usuario, ins.getUsuario());
    }


    @Test
    @DisplayName("Debe lanzar excepción si usuario no existe")
    void inscribir_usuarioNoExiste() {

        when(usuarioRepo.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.inscribir(1L, 100));

        assertEquals("Usuario no encontrado", ex.getMessage());
    }


    @Test
    @DisplayName("Debe lanzar excepción si centro no existe")
    void inscribir_centroNoExiste() {

        when(usuarioRepo.findById(1L)).thenReturn(Optional.of(usuario));
        when(centroRepo.findById(100)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.inscribir(1L, 100));

        assertEquals("Centro no encontrado", ex.getMessage());
    }

    @Test
    @DisplayName("No debe guardar si ya está inscrito")
    void inscribir_yaInscrito() {

        when(usuarioRepo.findById(1L)).thenReturn(Optional.of(usuario));
        when(centroRepo.findById(100)).thenReturn(Optional.of(centro));
        when(inscripcionRepo.existsByUsuarioIdUserAndRut(1L, 100)).thenReturn(true);

        service.inscribir(1L, 100);

        verify(inscripcionRepo, never()).save(any());
    }

    @Test
    @DisplayName("Debe inscribir solo los que no están inscritos")
    void inscribirMasivo() {

        List<Long> usuarios = List.of(1L, 2L);

        Usuario u2 = new Usuario();
        u2.setIdUser(2L);
        u2.setCorreo("u2@test.com");
        u2.setTelefono("999");

        when(usuarioRepo.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepo.findById(2L)).thenReturn(Optional.of(u2));
        when(centroRepo.findById(100)).thenReturn(Optional.of(centro));

        // usuario 1 ya inscrito, usuario 2 no
        when(inscripcionRepo.existsByUsuarioIdUserAndRut(1L, 100)).thenReturn(true);
        when(inscripcionRepo.existsByUsuarioIdUserAndRut(2L, 100)).thenReturn(false);

        service.inscribirMasivo(100, usuarios);

        verify(inscripcionRepo, times(1)).save(any());
    }
}