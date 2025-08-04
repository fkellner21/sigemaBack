package com.example.sigema.controllers;

import com.example.sigema.models.*;
import com.example.sigema.models.enums.EstadoTramite;
import com.example.sigema.services.ITramitesService;
import com.example.sigema.services.implementations.NotificacionService;
import com.example.sigema.services.implementations.UsuarioService;
import com.example.sigema.utilidades.JwtUtils;
import com.example.sigema.utilidades.SigemaException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;

import java.util.*;

@RestController
@RequestMapping("/api/tramites")
//@CrossOrigin(origins = "*")
public class TramitesController {

    private final ITramitesService tramitesService;
    private final JwtUtils jwtUtils;

    @Autowired
    private HttpServletRequest request;
    @Autowired
    private UsuarioService usuarioService;

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
    public ResponseEntity<?> obtenerTodosConFechas(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate  desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate  hasta
    ) {
        try {
            Long idUnidad = jwtUtils.extractIdUnidad(getToken());
            String rol = jwtUtils.extractRol(getToken());

            if (Objects.equals(rol, "ROLE_ADMINISTRADOR") || Objects.equals(rol, "ROLE_BRIGADA")) {
                idUnidad = null;
            }

            ZoneId zone = ZoneId.of("America/Montevideo");

            // Si no viene fecha → asignar por defecto
            LocalDate localDesde = desde != null ? desde : LocalDate.now(zone).minusDays(7);
            LocalDate localHasta = hasta != null ? hasta : LocalDate.now(zone);

            Date fechaDesde = Date.from(localDesde.atStartOfDay(zone).toInstant());
            Date fechaHasta = Date.from(localHasta.atTime(LocalTime.MAX).atZone(zone).toInstant());

            List<Tramite> tramites = tramitesService.ObtenerTodosPorFechas(idUnidad, fechaDesde, fechaHasta);

            if (tramites == null) {
                tramites = new ArrayList<>();
            } else {
                tramites = new ArrayList<>(tramites); // Hacer mutable copia
            }

            tramites.sort((t1, t2) -> {
                if (t1.getId() == null && t2.getId() == null) return 0;
                if (t1.getId() == null) return 1;
                if (t2.getId() == null) return -1;
                return t2.getId().compareTo(t1.getId());
            });

            return ResponseEntity.ok().body(tramites);
        } catch (SigemaException e) {
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

        Long idUsuario= jwtUtils.extractIdUsuario(getToken());
        Usuario usuario=usuarioService.ObtenerPorId(idUsuario);
        Tramite tramite = tramitesService.ObtenerPorId(id, usuario).orElse(null);

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
    public ResponseEntity<?> editar(@PathVariable Long id, @RequestBody TramiteDTO tramiteDTO) {
        try {
            Long idUsuario= jwtUtils.extractIdUsuario(getToken());
            Tramite editado=tramitesService.Editar(id,tramiteDTO,idUsuario);
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
    public ResponseEntity<?> cambiarEstado(@PathVariable Long id, @RequestBody EstadoTramiteRequest estadoTramite) {
        try {
            Long idUsuario= jwtUtils.extractIdUsuario(getToken());
            EquipoActas equipoActas = tramitesService.CambiarEstado(id,estadoTramite.getEstadoTramite(),idUsuario);

            return ResponseEntity.ok().body(equipoActas.getActas());
        } catch(SigemaException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().body("Ha ocurrido un error, vuelva a intentarlo");
        }
    }
}