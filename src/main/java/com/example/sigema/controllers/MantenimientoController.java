package com.example.sigema.controllers;

import com.example.sigema.models.Mantenimiento;
import com.example.sigema.models.MantenimientoDTO;
import com.example.sigema.services.IMantenimientoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mantenimientos")
@CrossOrigin(origins = "*")
public class MantenimientoController {

    @Autowired
    private IMantenimientoService servicio;

    @GetMapping
    public ResponseEntity<?> obtenerTodos() {
        try {
            List<Mantenimiento> lista = servicio.obtenerTodos();
            return ResponseEntity.ok(lista);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener los mantenimientos: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody MantenimientoDTO mantenimiento) {
        try {
            Mantenimiento nuevo = servicio.crear(mantenimiento);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error al crear el mantenimiento: " + e.getMessage());
        }
    }

    @GetMapping("/equipo/{idEquipo}")
    public ResponseEntity<List<Mantenimiento>> obtenerPorEquipo(@PathVariable Long idEquipo) {
        try {
            List<Mantenimiento> mantenimientos = servicio.obtenerPorEquipo(idEquipo);
            return ResponseEntity.ok(mantenimientos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Mantenimiento> obtenerPorId(@PathVariable Long id) {
        try {
            return servicio.obtenerPorId(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }




    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@PathVariable Long id, @RequestBody MantenimientoDTO mantenimiento) {
        try {
            Mantenimiento actualizado = servicio.editar(id, mantenimiento);
            return ResponseEntity.ok(actualizado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error al actualizar el mantenimiento: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            servicio.eliminar(id);
            return ResponseEntity.ok("Mantenimiento eliminado con éxito");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar el mantenimiento: " + e.getMessage());
        }
    }
}
