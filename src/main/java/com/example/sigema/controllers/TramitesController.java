package com.example.sigema.controllers;

import com.example.sigema.models.Actuacion;
import com.example.sigema.models.Equipo;
import com.example.sigema.models.Tramite;
import com.example.sigema.models.TramiteDTO;
import com.example.sigema.models.enums.EstadoTramite;
import com.example.sigema.services.ITramitesService;
import com.example.sigema.utilidades.JwtUtils;
import com.example.sigema.utilidades.SigemaException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api/tramites")
@CrossOrigin(origins = "*")
public class TramitesController {

    private final ITramitesService tramitesService;
    private final JwtUtils jwtUtils;

    @Autowired
    private HttpServletRequest request;


    public TramitesController(ITramitesService tramitesService,  JwtUtils jwtUtils){
        this.tramitesService = tramitesService;
        this.jwtUtils = jwtUtils;
    }

    public String getToken() {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        throw new RuntimeException("No se encontró el token en el header");
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'BRIGADA', 'UNIDAD', 'ADMINISTRADOR_UNIDAD')")
    @GetMapping()
    public ResponseEntity<?> obtenerTodos() {
        try {
            Long idUnidad = jwtUtils.extractIdUnidad(getToken());
            String rol = jwtUtils.extractRol(getToken());

            if(Objects.equals(rol, "ROLE_ADMINISTRADOR") || Objects.equals(rol, "ROLE_BRIGADA")){
                idUnidad = null;
            }
            List<Tramite> tramites = tramitesService.ObtenerTodos(idUnidad);
            return ResponseEntity.ok().body(tramites);
        } catch(SigemaException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Ha ocurrido un error, vuelva a intentarlo");
        }
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'BRIGADA', 'UNIDAD', 'ADMINISTRADOR_UNIDAD')")
    @PostMapping
    public ResponseEntity<?> crearTramite(@RequestBody TramiteDTO tramite) {
        try {
            Long idUsuario= jwtUtils.extractIdUsuario(getToken());
            Tramite creado = tramitesService.Crear(tramite,idUsuario);
            return ResponseEntity.ok().body(creado);
        } catch(SigemaException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().body("Ha ocurrido un error, vuelva a intentarlo");
        }
    }
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'BRIGADA', 'UNIDAD', 'ADMINISTRADOR_UNIDAD')")
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id){
        try{
        Tramite tramite = tramitesService.ObtenerPorId(id).orElse(null);
        if(tramite==null){
            throw new SigemaException("Tramite no encontrado");
        }
        return ResponseEntity.ok().body(tramite);
        } catch(SigemaException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().body("Ha ocurrido un error, vuelva a intentarlo");
        }
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'BRIGADA','UNIDAD', 'ADMINISTRADOR_UNIDAD')")
    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@PathVariable Long id, @RequestBody TramiteDTO tramite) {
        try {
            Long idUsuario= jwtUtils.extractIdUsuario(getToken());
            Tramite editado=tramitesService.Editar(id,tramite,idUsuario);
            return ResponseEntity.ok().body(editado);
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
            tramitesService.Eliminar(id);

            return ResponseEntity.ok().body("Se ha eliminado con exito");
        } catch(SigemaException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().body("Ha ocurrido un error, vuelva a intentarlo");
        }
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'BRIGADA','UNIDAD', 'ADMINISTRADOR_UNIDAD')")
    @PostMapping("/{id}/actuacion")
    public ResponseEntity<?> crearActuacion(@PathVariable Long id, @RequestBody Actuacion actuacion) {
        try {
            Long idUsuario= jwtUtils.extractIdUsuario(getToken());
            Actuacion creado = tramitesService.CrearActuacion(id,actuacion,idUsuario);
            return ResponseEntity.ok().body(creado);
        } catch(SigemaException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().body("Ha ocurrido un error, vuelva a intentarlo");
        }
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'BRIGADA','UNIDAD', 'ADMINISTRADOR_UNIDAD')")
    @PostMapping("/{id}/estado")
    public ResponseEntity<?> cambiarEstado(@PathVariable Long id, @RequestBody EstadoTramite estadoTramite) {
        try {
            Long idUsuario= jwtUtils.extractIdUsuario(getToken());
            Tramite tramite = tramitesService.CambiarEstado(id,estadoTramite,idUsuario);
            return ResponseEntity.ok().body(tramite);
        } catch(SigemaException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().body("Ha ocurrido un error, vuelva a intentarlo");
        }
    }
}