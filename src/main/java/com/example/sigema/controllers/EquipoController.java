package com.example.sigema.controllers;

import com.example.sigema.models.Equipo;
import com.example.sigema.services.IEquipoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/equipos")
@CrossOrigin(origins = "*")
public class EquipoController {

    private final IEquipoService equiposService;

    @Autowired
    public EquipoController(IEquipoService equiposService) {
        this.equiposService = equiposService;
    }

    @GetMapping
    public ResponseEntity<?> obtenerTodos() {
        try{
            List<Equipo> equipos = equiposService.obtenerTodos();

            return ResponseEntity.ok().body(equipos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Equipo equipo) {
        try{
           Equipo creado = equiposService.Crear(equipo);

           return ResponseEntity.ok().body(creado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        try{
            equiposService.Eliminar(id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        try {
            Equipo equipo = equiposService.ObtenerPorId(id).orElse(null);

            return ResponseEntity.ok().body(equipo);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@PathVariable Long id, @RequestBody Equipo equipo) {
        try{
            Equipo editado = equiposService.Editar(id, equipo);

            return ResponseEntity.ok().body(editado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }








}
