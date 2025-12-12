package com.example.eva.service;

import com.example.eva.util.LoggerEva;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Service;

// Motor Thymeleaf
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.ArrayList;
import java.util.List;

@Service
public class CorreoService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final LoggerEva logger = LoggerEva.getInstancia();

    public CorreoService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }


    private boolean esAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return false;

        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    private void validarPermiso() {
        if (!esAdmin()) {
            throw new SecurityException("No autorizado: solo el ADMIN puede enviar correos.");
        }
    }


    public List<String> enviarCorreoMasivoUnoPorUno(List<String> destinatarios, String asunto, String html) {

        validarPermiso(); // 🔒 Seguridad

        List<String> errores = new ArrayList<>();

        logger.info("Iniciando envío uno-por-uno. Destinatarios: " + (destinatarios == null ? 0 : destinatarios.size()));

        if (destinatarios == null || destinatarios.isEmpty()) {
            logger.warning("Lista de destinatarios vacía.");
            return errores;
        }

        for (String destino : destinatarios) {

            if (destino == null || destino.isBlank()) continue;

            try {
                enviarCorreoHTML(destino, asunto, html);
                logger.info("Correo enviado a: " + destino);

            } catch (Exception ex) {
                logger.error("Fallo enviando a " + destino + " -> " + ex.getMessage());
                ex.printStackTrace();

                try {
                    Thread.sleep(1200);

                    enviarCorreoHTML(destino, asunto, html);
                    logger.info("Reintento exitoso: " + destino);

                } catch (Exception retryEx) {
                    logger.error("Reintento fallido para " + destino + " -> " + retryEx.getMessage());
                    retryEx.printStackTrace();
                    errores.add(destino);
                }
            }
        }

        logger.info("Envío terminado. Errores: " + errores.size());
        return errores;
    }


    public List<String> enviarCorreoMasivoBCC(List<String> destinatarios, String asunto, String html) {

        validarPermiso(); // 🔒 Seguridad

        List<String> errores = new ArrayList<>();
        if (destinatarios == null || destinatarios.isEmpty()) return errores;

        try {
            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");

            helper.setFrom("eva.siondeimpuesticos@gmail.com");
            helper.setTo("notificaciones@eva.com");
            helper.setSubject(asunto == null ? "" : asunto);
            helper.setText(html == null ? "" : html, true);

            for (String c : destinatarios) {
                try {
                    if (c == null || c.isBlank()) continue;
                    helper.addBcc(c.trim());
                } catch (Exception e) {
                    logger.error("Dirección inválida BCC: " + c + " -> " + e.getMessage());
                    errores.add(c);
                }
            }

            mailSender.send(mensaje);
            logger.info("Correo BCC enviado. Errores: " + errores.size());

        } catch (Exception e) {
            logger.error("ERROR general en envío BCC: " + e.getMessage());
            e.printStackTrace();
            errores.clear();
            errores.addAll(destinatarios);
        }

        return errores;
    }

 
    public List<String> enviarCorreoMasivoBatch(List<String> destinatarios, String asunto, String html) {

        validarPermiso(); // 🔒 Seguridad

        List<String> errores = new ArrayList<>();
        if (destinatarios == null || destinatarios.isEmpty()) return errores;

        int batchSize = 40;

        for (int i = 0; i < destinatarios.size(); i += batchSize) {

            List<String> batch = destinatarios.subList(i, Math.min(i + batchSize, destinatarios.size()));

            logger.info("Enviando batch [" + (i / batchSize + 1) + "] tamaño=" + batch.size());

            errores.addAll(enviarCorreoMasivoUnoPorUno(batch, asunto, html));

            try {
                Thread.sleep(2000);
            } catch (InterruptedException ignored) {}
        }

        return errores;
    }


    @Async("correoExecutor")
    public void enviarCorreoMasivoUnoPorUnoAsync(List<String> destinatarios, String asunto, String html) {
        enviarCorreoMasivoUnoPorUno(destinatarios, asunto, html);
    }

    @Async("correoExecutor")
    public void enviarCorreoMasivoBatchAsync(List<String> destinatarios, String asunto, String html) {
        enviarCorreoMasivoBatch(destinatarios, asunto, html);
    }


    private void enviarCorreoHTML(String destino, String asunto, String html) throws MessagingException {

        MimeMessage msg = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");

        helper.setFrom("eva.siondeimpuesticos@gmail.com");
        helper.setTo(destino);
        helper.setSubject(asunto == null ? "" : asunto);
        helper.setText(html == null ? "" : html, true);

        mailSender.send(msg);
    }

    public String generarCorreoHTML(String titulo, String mensaje, String nombreUsuario) {

        Context ctx = new Context();
        ctx.setVariable("titulo", titulo);
        ctx.setVariable("mensaje", mensaje);
        ctx.setVariable("nombre", nombreUsuario);

        return templateEngine.process("email", ctx);
    }
}
