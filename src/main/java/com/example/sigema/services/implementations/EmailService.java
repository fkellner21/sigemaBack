package com.example.sigema.services.implementations;

import com.example.sigema.models.Equipo;
import com.example.sigema.models.ModeloEquipo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void enviarAlertaMantenimiento(Equipo equipo, ModeloEquipo modelo, String asunto, boolean esCritico, String destinatario) {
        String to = "mantenimiento@tusistema.com";
        String body = "Equipo: " + equipo.getMatricula() +
                "\nUnidad: " + (equipo.getUnidad() != null ? equipo.getUnidad().getNombre() : "N/A") +
                "\nModelo: " + modelo.getModelo() +
                "\nCantidad actual: " + equipo.getCantidadUnidadMedida() + " " + modelo.getUnidadMedida() +
                "\nFrecuencia definida: " + modelo.getFrecuenciaUnidadMedida() + " " + modelo.getUnidadMedida();

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(asunto);
        message.setText(body);

        mailSender.send(message);
    }
}
