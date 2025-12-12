package com.example.eva.service;

import jakarta.mail.internet.MimeMessage;
import jakarta.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private SpringTemplateEngine templateEngine;

    public void enviarEmailNotificacion(String emailDestino, String nombre, String titulo, String mensajeHtml) {

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(emailDestino);
            helper.setSubject(titulo);

        
            Context context = new Context();
            context.setVariable("nombre", nombre);
            context.setVariable("titulo", titulo);
            context.setVariable("mensaje", mensajeHtml);
            context.setVariable("ctaUrl", "http://localhost:8080");

            
            String htmlContent = templateEngine.process("email", context);

            helper.setText(htmlContent, true);

            mailSender.send(message);

            System.out.println("Correo enviado a: " + emailDestino);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
