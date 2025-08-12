package com.example.sigema.services.implementations;

import com.example.sigema.models.Equipo;
import com.example.sigema.models.ModeloEquipo;
import com.example.sigema.services.ILogService;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private JavaMailSender mailSender;
    private final ILogService logService;

    @Autowired
    public EmailService(JavaMailSender mailSender, ILogService logService) {
        this.mailSender = mailSender;
        this.logService = logService;
    }

    public void enviarAlertaMantenimiento(Equipo equipo, ModeloEquipo modelo, String contenidoHtml, boolean esCritico, String destinatario) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(destinatario);
            helper.setSubject(esCritico ? "🔴 ALERTA DE MANTENIMIENTO CRÍTICA" : "⚠️ Alerta preventiva de mantenimiento");
            helper.setText(contenidoHtml, true);

            mailSender.send(message);
            logService.guardarLog("Se ha enviado el email de alerta para el equipo (Matrícula: " + equipo.getMatricula() + ", Modelo: " + equipo.getModeloEquipo().getModelo() + ")", false);
        } catch (Exception e) {
            logService.guardarLog("No se ha enviado el email de alerta para el equipo (Matrícula: " + equipo.getMatricula() + ", Modelo: " + equipo.getModeloEquipo().getModelo() + "), Error: " + e.getMessage(), false);
        }
    }
}