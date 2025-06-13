package com.example.sigema.controllers;

import com.example.sigema.models.Grado;
import com.example.sigema.services.IGradoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/grados")
public class GradoController {

    private final IGradoService gradoService;

    @Autowired
    public GradoController(IGradoService gradoService) {
        this.gradoService = gradoService;
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Grado grado) {
        try {
            Grado nuevo = gradoService.Crear(grado);
            return ResponseEntity.ok(nuevo);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@PathVariable Long id, @RequestBody Grado grado) {
        try {
            Grado actualizado = gradoService.Editar(id, grado);
            return ResponseEntity.ok(actualizado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            gradoService.Eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        try {
            Grado grado = gradoService.ObtenerPorId(id);
            return ResponseEntity.ok(grado);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

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
