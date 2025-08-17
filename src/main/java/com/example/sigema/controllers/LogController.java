package com.example.sigema.controllers;

import com.example.sigema.models.Equipo;
import com.example.sigema.services.IEquipoService;
import com.example.sigema.services.ILogService;
import com.example.sigema.utilidades.JwtUtils;
import com.example.sigema.utilidades.SigemaException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/logs")
public class LogController {

    private final ILogService logService;
    private final JwtUtils jwtUtils;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    public LogController(ILogService logService, JwtUtils jwtUtils) {
        this.logService = logService;
        this.jwtUtils = jwtUtils;
    }

    public String getToken() {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        throw new RuntimeException("No se encontró el token en el header");
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR')")
    @GetMapping
    public ResponseEntity<?> obtenerTodos() {
        try {
            String rol = jwtUtils.extractRol(getToken());
            List<String> logs = new ArrayList<>();

            if(Objects.equals(rol, "ROLE_ADMINISTRADOR")){
                logs = logService.listarLogsDisponibles();
            }else{
                logs = new ArrayList<>();
            }

            return ResponseEntity.ok().body(logs);
        } catch(SigemaException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().body("Ha ocurrido un error, vuelva a intentarlo");
        }
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR')")
    @GetMapping("/descargar/{fecha}")
    public ResponseEntity<Resource> descargarLogPorFecha(@PathVariable String fecha) throws IOException {
        String rol = jwtUtils.extractRol(getToken());
        Resource resource = logService.descargarLogPorFecha(fecha);

        if(Objects.equals(rol, "ROLE_ADMINISTRADOR")){
            if (resource == null) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=log_" + fecha + ".gz")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        }

        return ResponseEntity.notFound().build();
    }
}