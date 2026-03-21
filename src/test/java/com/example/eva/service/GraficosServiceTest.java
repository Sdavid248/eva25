package com.example.eva.service;

import com.example.eva.repository.InscripcionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@org.junit.jupiter.api.extension.ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class GraficosServiceTest {

    @Mock
    private InscripcionRepository inscripcionRepository;

    @InjectMocks
    private GraficosService service;

    @Test
    @DisplayName("Debe generar gráfico de usuarios por estado")
    void generarGraficoUsuariosEstadoPorCentro_ok() throws Exception {

        when(inscripcionRepository.countUsuariosEstadoPorCentro())
                .thenReturn(List.of(
                        new Object[]{"Centro 1", "ACTIVO", 10L},
                        new Object[]{"Centro 1", "INACTIVO", 5L},
                        new Object[]{"Centro 2", "ACTIVO", 7L}
                ));

        byte[] resultado = service.generarGraficoUsuariosEstadoPorCentro(800, 600);

        assertNotNull(resultado);
        assertTrue(resultado.length > 0);

        verify(inscripcionRepository).countUsuariosEstadoPorCentro();
    }

    @Test
    @DisplayName("Debe generar gráfico lineal de usuarios")
    void generarGraficoLinealUsuarios_ok() throws Exception {

        when(inscripcionRepository.countInscripcionesPorUsuario())
                .thenReturn(List.of(
                        new Object[]{"Juan", 5L},
                        new Object[]{"Ana", 3L}
                ));

        byte[] resultado = service.generarGraficoLinealUsuarios(800, 600);

        assertNotNull(resultado);
        assertTrue(resultado.length > 0);

        verify(inscripcionRepository).countInscripcionesPorUsuario();
    }

    @Test
    @DisplayName("Debe generar gráfico de torta")
    void generarGraficoTortaCentros_ok() throws Exception {

        when(inscripcionRepository.countUsuariosEstadoPorCentro())
                .thenReturn(List.of(
                        new Object[]{"Centro 1", "ACTIVO", 10L},
                        new Object[]{"Centro 1", "INACTIVO", 5L},
                        new Object[]{"Centro 2", "ACTIVO", 7L}
                ));

        byte[] resultado = service.generarGraficoTortaCentros(800, 600);

        assertNotNull(resultado);
        assertTrue(resultado.length > 0);

        verify(inscripcionRepository).countUsuariosEstadoPorCentro();
    }

    @Test
    @DisplayName("Debe generar PDF básico")
    void generarReportePDF_ok() throws Exception {

        byte[] resultado = service.generarReportePDF();

        assertNotNull(resultado);
        assertTrue(resultado.length > 0);

        String contenido = new String(resultado);

        assertTrue(contenido.contains("%PDF"));
    }

    @Test
    @DisplayName("Debe funcionar aunque no haya datos")
    void generarGrafico_sinDatos() throws Exception {

        when(inscripcionRepository.countUsuariosEstadoPorCentro())
                .thenReturn(List.of());

        byte[] resultado = service.generarGraficoUsuariosEstadoPorCentro(800, 600);

        assertNotNull(resultado);
        assertTrue(resultado.length > 0);
    }
}