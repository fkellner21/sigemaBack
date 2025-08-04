package com.example.sigema.controllers;


import com.example.sigema.models.Repuesto;
import com.example.sigema.models.enums.TipoRepuesto;
import com.example.sigema.services.IRepuestoService;
import com.example.sigema.utilidades.SigemaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@RestController
@RequestMapping("/api/repuestos")
//@CrossOrigin(origins = "*")
public class RepuestoController {

    private final IRepuestoService repuestosService;

    @Autowired
    public RepuestoController(IRepuestoService r, IRepuestoService repuestoService) {
        this.repuestosService = repuestoService;
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'BRIGADA', 'UNIDAD', 'ADMINISTRADOR_UNIDAD')")
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Repuesto repuesto) {
        try {
            Repuesto creado = repuestosService.Crear(repuesto);

            return ResponseEntity.ok().body(creado);
        } catch(SigemaException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().body("Ha ocurrido un error, vuelva a intentarlo");
        }
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'BRIGADA', 'UNIDAD', 'ADMINISTRADOR_UNIDAD')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            //repuestosService.Eliminar(id);

            return ResponseEntity.ok().body("Se ha eliminado con exito");
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
            Optional<Repuesto> repuesto = repuestosService.ObtenerPorId(id);

            return ResponseEntity.ok().body(repuesto);
        } catch(SigemaException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().body("Ha ocurrido un error, vuelva a intentarlo");
        }
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'BRIGADA', 'UNIDAD', 'ADMINISTRADOR_UNIDAD')")
    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@PathVariable Long id, @RequestBody Repuesto repuesto) {
        try {
            Repuesto editado = repuestosService.Editar(id, repuesto);

            return ResponseEntity.ok().body(editado);
        } catch(SigemaException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().body("Ha ocurrido un error, vuelva a intentarlo");
        }
    }
}
