package com.example.eva.service;

import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import static org.mockito.Mockito.*;

@org.junit.jupiter.api.extension.ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private SpringTemplateEngine templateEngine;

    @InjectMocks
    private EmailService emailService;

    @Mock
    private MimeMessage mimeMessage;

    @BeforeEach
    void setUp() {
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    }

    @Test
    @DisplayName("Debe enviar email correctamente")
    void enviarEmailNotificacion_debeEnviarCorreo() {

        String email = "test@test.com";
        String nombre = "Juan";
        String titulo = "Bienvenido";
        String mensaje = "<p>Hola</p>";

        when(templateEngine.process(eq("email"), any(Context.class)))
                .thenReturn("<html>Email renderizado</html>");

        emailService.enviarEmailNotificacion(email, nombre, titulo, mensaje);

        verify(mailSender).createMimeMessage();
        verify(templateEngine).process(eq("email"), any(Context.class));
        verify(mailSender).send(mimeMessage);
    }

    @Test
    @DisplayName("Debe procesar correctamente variables del contexto")
    void enviarEmailNotificacion_debeUsarContextoCorrectamente() {

        when(templateEngine.process(eq("email"), any(Context.class)))
                .thenReturn("<html>Email</html>");

        emailService.enviarEmailNotificacion(
                "correo@test.com",
                "Ana",
                "Titulo prueba",
                "<p>Mensaje</p>"
        );

        ArgumentCaptor<Context> contextCaptor = ArgumentCaptor.forClass(Context.class);

        verify(templateEngine).process(eq("email"), contextCaptor.capture());

        Context context = contextCaptor.getValue();

        assert context.getVariable("nombre").equals("Ana");
        assert context.getVariable("titulo").equals("Titulo prueba");
        assert context.getVariable("mensaje").equals("<p>Mensaje</p>");
        assert context.getVariable("ctaUrl").equals("http://localhost:8080");
    }

    @Test
    @DisplayName("No debe romper si ocurre una excepción")
    void enviarEmailNotificacion_siFalla_noDebeRomper() {

        when(templateEngine.process(eq("email"), any(Context.class)))
                .thenThrow(new RuntimeException("Error en plantilla"));

        emailService.enviarEmailNotificacion(
                "correo@test.com",
                "Ana",
                "Titulo",
                "<p>Mensaje</p>"
        );

        verify(mailSender, never()).send(any(MimeMessage.class));
    }
}