package com.example.sigema.controllers;

import com.example.sigema.models.Mantenimiento;
import com.example.sigema.models.MantenimientoDTO;
import com.example.sigema.models.Tramite;
import com.example.sigema.services.IMantenimientoService;
import com.example.sigema.utilidades.JwtUtils;
import com.example.sigema.utilidades.SigemaException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/mantenimientos")
//@CrossOrigin(origins = "*")
public class MantenimientoController {

    @Autowired
    private IMantenimientoService servicio;
    private final JwtUtils jwtUtils;

    @Autowired
    private HttpServletRequest request;

    public MantenimientoController(JwtUtils jwtUtils) {
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
    @GetMapping
    public ResponseEntity<?> obtenerTodos() {
        try {
            List<Mantenimiento> lista = servicio.obtenerTodos();
            return ResponseEntity.ok(lista);
        } catch(SigemaException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener los mantenimientos: " + e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'BRIGADA', 'UNIDAD', 'ADMINISTRADOR_UNIDAD')")
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody MantenimientoDTO mantenimiento) {
        try {
            Mantenimiento nuevo = servicio.crear(mantenimiento);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
        } catch(SigemaException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error al crear el mantenimiento: " + e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'BRIGADA', 'UNIDAD', 'ADMINISTRADOR_UNIDAD')")
    @GetMapping("/equipo/{idEquipo}")
    public ResponseEntity<List<Mantenimiento>> obtenerPorEquipo(@PathVariable Long idEquipo) {
        try {
            List<Mantenimiento> mantenimientos = servicio.obtenerPorEquipo(idEquipo);
            return ResponseEntity.ok(mantenimientos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'BRIGADA', 'UNIDAD', 'ADMINISTRADOR_UNIDAD')")
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

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'BRIGADA', 'ADMINISTRADOR_UNIDAD')")
    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@PathVariable Long id, @RequestBody MantenimientoDTO mantenimiento) {
        try {
            Mantenimiento actualizado = servicio.editar(id, mantenimiento);
            return ResponseEntity.ok(actualizado);
        } catch(SigemaException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error al actualizar el mantenimiento: " + e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'BRIGADA', 'ADMINISTRADOR_UNIDAD')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            servicio.eliminar(id);
            return ResponseEntity.ok().build();
        } catch(SigemaException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar el mantenimiento: " + e.getMessage());
        }
    }


    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'BRIGADA', 'UNIDAD', 'ADMINISTRADOR_UNIDAD')")
    @GetMapping("/filtrar")
    public ResponseEntity<?> obtenerTodosConFechas(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta
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

            List<Mantenimiento> mantenimientos = servicio.ObtenerTodosPorFechas(idUnidad, fechaDesde, fechaHasta);

            if (mantenimientos == null) {
                mantenimientos = new ArrayList<>();
            } else {
                mantenimientos = new ArrayList<>(mantenimientos);
            }

            mantenimientos.sort((t1, t2) -> {
                if (t1.getId() == null && t2.getId() == null) return 0;
                if (t1.getId() == null) return 1;
                if (t2.getId() == null) return -1;
                return t2.getId().compareTo(t1.getId());
            });

            return ResponseEntity.ok().body(mantenimientos);

        } catch(SigemaException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}
