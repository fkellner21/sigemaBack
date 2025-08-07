package com.example.sigema.controllers;

import com.example.sigema.models.UnidadEmail;
import com.example.sigema.services.IUnidadEmailService;
import com.example.sigema.utilidades.SigemaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/unidad-emails")
//@CrossOrigin(origins = "*")
public class UnidadEmailController {

    @Autowired
    private IUnidadEmailService unidadEmailService;

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'BRIGADA', 'UNIDAD', 'ADMINISTRADOR_UNIDAD')")
    @PostMapping("/{unidadId}")
    public ResponseEntity<?> agregarEmail(@PathVariable Long unidadId, @RequestBody UnidadEmail email) {
        try {
            UnidadEmail creado = unidadEmailService.agregarEmail(unidadId, email.getEmail());
            return ResponseEntity.ok(creado);
        } catch (SigemaException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al agregar el email");
        }
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'BRIGADA', 'UNIDAD', 'ADMINISTRADOR_UNIDAD')")
    @DeleteMapping("/{emailId}")
    public ResponseEntity<?> eliminarEmail(@PathVariable Long emailId) {
        try {
            unidadEmailService.eliminarEmail(emailId);
            return ResponseEntity.ok("Email eliminado");
        } catch (SigemaException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'BRIGADA', 'UNIDAD', 'ADMINISTRADOR_UNIDAD')")
    @GetMapping("/{unidadId}")
    public ResponseEntity<?> obtenerEmailsPorUnidad(@PathVariable Long unidadId) {
        try {
            return ResponseEntity.ok(unidadEmailService.obtenerEmailsPorUnidad(unidadId));
        } catch (SigemaException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al obtener los emails");
        }
    }

}
