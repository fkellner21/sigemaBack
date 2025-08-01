package com.example.sigema.controllers;

import com.example.sigema.models.Notificacion;
import com.example.sigema.models.Repuesto;
import com.example.sigema.models.enums.TipoRepuesto;
import com.example.sigema.services.INotificacionesService;
import com.example.sigema.utilidades.JwtUtils;
import com.example.sigema.utilidades.SigemaException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notificaciones")
//@CrossOrigin(origins = "*")
public class NotificacionesController {

    private final JwtUtils jwtUtils;

    @Autowired
    private final INotificacionesService notificacionesService;

    @Autowired
    private HttpServletRequest request;

    public String getToken() {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        throw new RuntimeException("No se encontró el token en el header");
    }

    public NotificacionesController(JwtUtils jwtUtils, INotificacionesService notificacionesService){
        this.jwtUtils = jwtUtils;
        this.notificacionesService = notificacionesService;
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'BRIGADA', 'UNIDAD', 'ADMINISTRADOR_UNIDAD')")
    @GetMapping()
    public ResponseEntity<?> obtenerTodas() {
        try {
            Long idUsuario= jwtUtils.extractIdUsuario(getToken());
            List<Notificacion> notificaciones = notificacionesService.obtenerPorIdUsuario(idUsuario);

            return ResponseEntity.ok().body(notificaciones);
        } catch(SigemaException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().body("Ha ocurrido un error, vuelva a intentarlo");
        }
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'BRIGADA', 'UNIDAD', 'ADMINISTRADOR_UNIDAD')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            notificacionesService.Eliminar(id);

            return ResponseEntity.ok().build();
        } catch(SigemaException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().body("Ha ocurrido un error, vuelva a intentarlo");
        }
    }
}