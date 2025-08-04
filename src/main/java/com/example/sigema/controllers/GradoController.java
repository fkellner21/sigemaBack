package com.example.sigema.controllers;

import com.example.sigema.models.Grado;
import com.example.sigema.services.IGradoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/grados")
//@CrossOrigin(origins = "*")
public class GradoController {

    private final IGradoService gradoService;

    @Autowired
    public GradoController(IGradoService gradoService) {
        this.gradoService = gradoService;
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR')")
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Grado grado) {
        try {
            Grado nuevo = gradoService.Crear(grado);
            return ResponseEntity.ok(nuevo);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR')")
    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@PathVariable Long id, @RequestBody Grado grado) {
        try {
            Grado actualizado = gradoService.Editar(id, grado);
            return ResponseEntity.ok(actualizado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            gradoService.Eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR')")
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        try {
            Grado grado = gradoService.ObtenerPorId(id);
            return ResponseEntity.ok(grado);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'BRIGADA', 'UNIDAD', 'ADMINISTRADOR_UNIDAD')")
    @GetMapping
    public ResponseEntity<List<Grado>> obtenerTodos() {
        try {
            List<Grado> grados = gradoService.obtenerTodos();
            return ResponseEntity.ok(grados);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
