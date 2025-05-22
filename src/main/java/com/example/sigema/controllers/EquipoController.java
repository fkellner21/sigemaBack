package com.example.sigema.controllers;

import com.example.sigema.models.Equipo;
import com.example.sigema.repositories.IEquipoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

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
    public ResponseEntity<?> listar() {
        try{
            List<Equipo> equipos = repositorioEquipo.Listar();
               return ResponseEntity.ok().body(equipos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }


    }

    @PostMapping
    public ResponseEntity<?> agregar(@RequestBody Equipo equipo) {
        try{
           repositorioEquipo.Agregar(equipo);
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
    public ResponseEntity<?> buscar(@PathVariable Long id) {
        try {
            Equipo equipo = repositorioEquipo.Buscar(id);


            return ResponseEntity.ok().body(equipo);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> modificar(@PathVariable Long id, @RequestBody Equipo equipo) {
        try{
            equipo.setId(id);
            repositorioEquipo.Modificar(equipo);
            return ResponseEntity.ok().body(equipo);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }








}
