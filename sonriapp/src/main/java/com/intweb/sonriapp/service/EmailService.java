package com.intweb.sonriapp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.base-url}")
    private String baseUrl;

    public void enviarActivacionCuenta(String toEmail, String nombreCompleto, String token) {
        String link = baseUrl + "/set-password?token=" + token + "&tipo=ACTIVACION";
        String asunto = "Bienvenido a Sonriapp - Activa tu cuenta";
        String cuerpo = "Estimado/a " + nombreCompleto + ",\n\n"
                + "Tu cuenta en Sonriapp ha sido creada exitosamente.\n\n"
                + "Para activar tu cuenta y establecer tu contraseña, haz clic en el siguiente enlace:\n\n"
                + link + "\n\n"
                + "Este enlace es válido por 48 horas.\n\n"
                + "Si no esperabas este correo, por favor ignóralo.\n\n"
                + "Atentamente,\nEl equipo de Sonriapp";
        enviar(toEmail, asunto, cuerpo);
    }

    public void enviarResetPassword(String toEmail, String nombreCompleto, String token) {
        String link = baseUrl + "/set-password?token=" + token + "&tipo=RESET";
        String asunto = "Sonriapp - Cambio de contraseña";
        String cuerpo = "Estimado/a " + nombreCompleto + ",\n\n"
                + "El administrador ha solicitado un cambio de contraseña para tu cuenta.\n\n"
                + "Para establecer tu nueva contraseña, haz clic en el siguiente enlace:\n\n"
                + link + "\n\n"
                + "Este enlace es válido por 24 horas.\n\n"
                + "Si no solicitaste este cambio, contacta con el administrador.\n\n"
                + "Atentamente,\nEl equipo de Sonriapp";
        enviar(toEmail, asunto, cuerpo);
    }

    private void enviar(String to, String asunto, String cuerpo) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(asunto);
            helper.setText(cuerpo, false);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Error al enviar correo a " + to + ": " + e.getMessage(), e);
        }
    }
}