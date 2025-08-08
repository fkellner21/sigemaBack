package com.example.sigema.services.implementations;

import com.example.sigema.models.Equipo;
import com.example.sigema.models.ModeloEquipo;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private JavaMailSender mailSender;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void enviarAlertaMantenimiento(Equipo equipo, ModeloEquipo modelo, String contenidoHtml, boolean esCritico, String destinatario) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(destinatario);
            helper.setSubject(esCritico ? "🔴 ALERTA DE MANTENIMIENTO CRÍTICA" : "⚠️ Alerta preventiva de mantenimiento");
            helper.setText(contenidoHtml, true);

            mailSender.send(message);
        } catch (Exception e) {
            // Podés loguearlo o lanzar una excepción personalizada
            System.err.println("Error al enviar el correo: " + e.getMessage());
        }
    }
}
