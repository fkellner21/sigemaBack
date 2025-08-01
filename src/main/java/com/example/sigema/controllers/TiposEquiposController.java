package com.example.sigema.controllers;

import com.example.sigema.models.TipoEquipo;
import com.example.sigema.services.ITiposEquiposService;
import com.example.sigema.utilidades.SigemaException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tiposEquipos")
//@CrossOrigin(origins = "*")
public class TiposEquiposController {

    private final ITiposEquiposService tiposEquiposService;

    public TiposEquiposController(ITiposEquiposService tiposEquiposService) {
        this.tiposEquiposService = tiposEquiposService;
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'BRIGADA', 'UNIDAD', 'ADMINISTRADOR_UNIDAD')")
    @GetMapping("/activos/{soloActivos}")
    public ResponseEntity<?> obtenerTodos(@PathVariable boolean soloActivos) {
        try {
            List<TipoEquipo> tiposEquipos = tiposEquiposService.ObtenerTodos(soloActivos);

            return ResponseEntity.ok(tiposEquipos);
        } catch(SigemaException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().body("Ha ocurrido un error, vuelva a intentarlo");
        }
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'BRIGADA', 'UNIDAD', 'ADMINISTRADOR_UNIDAD')")
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        try {
            TipoEquipo tipoEquipo = tiposEquiposService.ObtenerPorId(id).orElse(null);

            if (tipoEquipo == null) {
                throw new SigemaException("Tipo de equipo no encontrado");
            }

            return ResponseEntity.ok(tipoEquipo);
        } catch(SigemaException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().body("Ha ocurrido un error, vuelva a intentarlo");
        }
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'BRIGADA')")
    @PostMapping("/")
    public ResponseEntity<?> crear(@RequestBody TipoEquipo tipoEquipo) {
        try {
            TipoEquipo tipoEquipoCreado = tiposEquiposService.Crear(tipoEquipo);

            return ResponseEntity.ok(tipoEquipoCreado);
        } catch(SigemaException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().body("Ha ocurrido un error, vuelva a intentarlo");
        }
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'BRIGADA')")
    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@PathVariable Long id, @RequestBody TipoEquipo tipoEquipo) {
        try {
            TipoEquipo tipoEquipoEditado = tiposEquiposService.Editar(id, tipoEquipo);

            return ResponseEntity.ok(tipoEquipoEditado);
        } catch(SigemaException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().body("Ha ocurrido un error, vuelva a intentarlo");
        }
    }
}
