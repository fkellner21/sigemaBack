package com.example.sigema.controllers;

import com.example.sigema.models.ModeloEquipo;
import com.example.sigema.services.IModeloEquipoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/modelosEquipo")
@CrossOrigin(origins = "*")
public class ModeloEquipoController {

    private final IModeloEquipoService modeloEquipoService;

    @Autowired
    public ModeloEquipoController(IModeloEquipoService modelEquipoService) {
        this.modeloEquipoService = modelEquipoService;
    }

    @GetMapping
    public ResponseEntity<?> obtenerTodos() {
        try {
            List<ModeloEquipo> modeloEquipos = modeloEquipoService.ObtenerTodos();
            return ResponseEntity.ok().body(modeloEquipos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody ModeloEquipo modeloEquipo) {
        try {
            ModeloEquipo model = modeloEquipoService.Crear(modeloEquipo);
            return ResponseEntity.ok().body(model);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerModeloEquipo(@PathVariable Long id) {
        try {
            ModeloEquipo model = modeloEquipoService.ObtenerPorId(id).orElse(null);
            return ResponseEntity.ok().body(model);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@PathVariable Long id, @RequestBody ModeloEquipo modeloEquipo) {
        try {
            ModeloEquipo model = modeloEquipoService.Editar(id, modeloEquipo);
            return ResponseEntity.ok().body(model);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


}
