package com.example.sigema.controllers;

import com.example.sigema.models.Marca;
import com.example.sigema.services.IMarcaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/marcas")
@CrossOrigin(origins = "*")
public class MarcaController {

    private final IMarcaService marcasService;

    @Autowired
    public MarcaController(IMarcaService marcasService) {
        this.marcasService = marcasService;
    }

    @GetMapping
    public ResponseEntity<?> obtenerTodos() {
        try {
            List<Marca> marcas = marcasService.ObtenerTodos();

            return ResponseEntity.ok().body(marcas);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Marca marca) {
        try {
            Marca creado = marcasService.Crear(marca);

            return ResponseEntity.ok().body(creado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        try {
            Marca marca = marcasService.ObtenerPorId(id).orElse(null);

            return ResponseEntity.ok().body(marca);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@PathVariable Long id, @RequestBody Marca marca) {
        try {
            Marca editado = marcasService.Editar(id, marca);

            return ResponseEntity.ok().body(editado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}