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

    private final IEquipoService repositorioEquipo;

    @Autowired
    public EquipoController(IEquipoService repoEquipo) {
        this.repositorioEquipo = repoEquipo;
    }

    @GetMapping
    public ResponseEntity<?> obtenerTodos() {
        try{
            List<Equipo> equipos = repositorioEquipo.obtenerTodos();
               return ResponseEntity.ok().body(equipos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }


    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Equipo equipo) {
        try{
           repositorioEquipo.Crear(equipo);
           return ResponseEntity.ok().body(equipo);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    //NOTERMINADO
    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        try{
            repositorioEquipo.Eliminar(id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        try {
            Optional<Equipo> equipo = repositorioEquipo.obtenerPorId(id);


            return ResponseEntity.ok().body(equipo);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@PathVariable Long id, @RequestBody Equipo equipo) {
        try{
            equipo.setId(id);
            repositorioEquipo.Editar(equipo);
            return ResponseEntity.ok().body(equipo);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }








}
