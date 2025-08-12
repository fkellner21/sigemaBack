package com.example.sigema.services.implementations;

import com.example.sigema.models.Usuario;
import com.example.sigema.repositories.IRepositoryUsuario;
import com.example.sigema.services.ILogService;
import com.example.sigema.utilidades.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Service
public class LogService implements ILogService {
    private static final Logger logger = LoggerFactory.getLogger("SigemaLogs");
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private IRepositoryUsuario repositoryUsuario;

    @Override
    public void guardarLog(String mensaje, boolean mostrarUsuario) {
        String nombreUsuario = "anonimo";
        Usuario usuario;

        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                Long idUsuario = jwtUtils.extractIdUsuario(token);
                usuario = repositoryUsuario.findById(idUsuario).orElse(null);
                assert usuario != null;
                nombreUsuario = usuario.getNombreCompleto() + " (CI: " + usuario.getCedula() + ")";
            }
        } catch (Exception e) {

        }

        String fechaHora = LocalDateTime.now().format(formatter);
        String mensajeCompleto = "";

        if(mostrarUsuario){
            mensajeCompleto = fechaHora + " - Usuario: " + nombreUsuario + " - " + mensaje;
        }else{
            mensajeCompleto = fechaHora + " - " + mensaje;
        }

        logger.info(mensajeCompleto);
    }
}
