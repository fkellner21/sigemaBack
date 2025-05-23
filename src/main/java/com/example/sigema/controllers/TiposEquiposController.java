package com.example.sigema.controllers;

import com.example.sigema.models.TipoEquipo;
import com.example.sigema.services.ITiposEquiposService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tiposEquipos")
public class TiposEquiposController {

    private final ITiposEquiposService tiposEquiposService;

    public TiposEquiposController(ITiposEquiposService tiposEquiposService){
        this.tiposEquiposService = tiposEquiposService;
    }

    @GetMapping("/activos/{soloActivos}")
    public ResponseEntity<?> obtenerTodos(@PathVariable boolean soloActivos){
        try {
            List<TipoEquipo> tiposEquipos = tiposEquiposService.ObtenerTodos(soloActivos);

            return ResponseEntity.ok(tiposEquipos);
        }catch (Exception ex){
            //Chequear despues los tipos de expceciones segun errores

            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id){
        try {
            TipoEquipo tipoEquipo = tiposEquiposService.ObtenerPorId(id).orElse(null);

            if(tipoEquipo == null){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tipo de equipo no encontrado");
            }

            return ResponseEntity.ok(tipoEquipo);
        }catch (Exception ex){
            //Chequear despues los tipos de expceciones segun errores

            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PostMapping("/")
    public ResponseEntity<?> crear(@RequestBody TipoEquipo tipoEquipo){
        try {
            TipoEquipo tipoEquipoCreado = tiposEquiposService.Crear(tipoEquipo);

            return ResponseEntity.ok(tipoEquipoCreado);
        }catch (Exception ex){
            //Chequear despues los tipos de expceciones segun errores

            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@PathVariable Long id, @RequestBody TipoEquipo tipoEquipo){
        try {
            TipoEquipo tipoEquipoEditado = tiposEquiposService.Editar(id, tipoEquipo);

            return ResponseEntity.ok(tipoEquipoEditado);
        }catch (Exception ex){
            //Chequear despues los tipos de expceciones segun errores

            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}
