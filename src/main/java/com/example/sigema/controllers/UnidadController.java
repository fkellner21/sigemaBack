package com.example.sigema.controllers;

import com.example.sigema.models.Equipo;
import com.example.sigema.models.Unidad;
import com.example.sigema.services.IEquipoService;
import com.example.sigema.services.IUnidadService;
import com.example.sigema.utilidades.SigemaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/unidades")
//@CrossOrigin(origins = "*")
public class UnidadController {

    private final IUnidadService unidadService;

    @Autowired
    public UnidadController(IUnidadService uniService) {
        this.unidadService = uniService;
    }


    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'BRIGADA', 'UNIDAD', 'ADMINISTRADOR_UNIDAD')")
    @GetMapping
    public ResponseEntity<?> obtenerTodos() {
        try {
            List<Unidad> equipos = unidadService.obtenerTodos();

            return ResponseEntity.ok().body(equipos);
        } catch(SigemaException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().body("Ha ocurrido un error, vuelva a intentarlo");
        }
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR')")
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Unidad equipo) {
        try {
            Unidad creado = unidadService.Crear(equipo);

            return ResponseEntity.ok().body(creado);
        } catch(SigemaException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().body("Ha ocurrido un error, vuelva a intentarlo");
        }
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            unidadService.Eliminar(id);

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
            Unidad equipo = unidadService.ObtenerPorId(id).orElse(null);

            return ResponseEntity.ok().body(equipo);
        } catch(SigemaException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().body("Ha ocurrido un error, vuelva a intentarlo");
        }
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR')")
    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@PathVariable Long id, @RequestBody Unidad unidad) {
        try {
            Unidad editado = unidadService.Editar(id, unidad);
            return ResponseEntity.ok().body(editado);
        } catch(SigemaException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().body("Ha ocurrido un error, vuelva a intentarlo");
        }
    }
}
