package com.example.sigema.controllers;

import com.example.sigema.models.*;
import com.example.sigema.models.enums.EstadoTramite;
import com.example.sigema.models.enums.TipoTramite;
import com.example.sigema.services.IEquipoService;
import com.example.sigema.services.ITramitesService;
import com.example.sigema.services.IUnidadService;
import com.example.sigema.services.implementations.TramiteService;
import com.example.sigema.utilidades.JwtUtils;
import com.example.sigema.utilidades.SigemaException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/equipos")
//@CrossOrigin(origins = "*")
public class EquipoController {

    private final IEquipoService equiposService;
    private final JwtUtils jwtUtils;

    @Autowired
    private HttpServletRequest request;
    @Autowired
    private IUnidadService unidadService;
    @Autowired
    private ITramitesService tramiteService;

    @Autowired
    public EquipoController(IEquipoService equiposService, JwtUtils jwtUtils) {
        this.equiposService = equiposService;
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
            Long idUnidad = jwtUtils.extractIdUnidad(getToken());
            String rol = jwtUtils.extractRol(getToken());

            if(Objects.equals(rol, "ROLE_ADMINISTRADOR") || Objects.equals(rol, "ROLE_BRIGADA")){
                idUnidad = null;
            }

            List<Equipo> equipos = equiposService.obtenerTodos(idUnidad);

            return ResponseEntity.ok().body(equipos);
        } catch(SigemaException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().body("Ha ocurrido un error, vuelva a intentarlo");
        }
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'BRIGADA', 'UNIDAD', 'ADMINISTRADOR_UNIDAD')")
    @GetMapping("/dashboard")
    public ResponseEntity<?> obtenerTodosDashboard() {
        try {
            Long idUnidad = jwtUtils.extractIdUnidad(getToken());
            String rol = jwtUtils.extractRol(getToken());

            if(Objects.equals(rol, "ROLE_ADMINISTRADOR") || Objects.equals(rol, "ROLE_BRIGADA")){
                idUnidad = null;
            }

            List<Equipo> equipos = equiposService.obtenerTodos(idUnidad);
            List<EquipoDashboardDTO> equiposDashboard = new ArrayList<EquipoDashboardDTO>();

            for (Equipo equipo : equipos) {
                EquipoDashboardDTO equipoDto = new EquipoDashboardDTO();
                equipoDto.fromEquipo(equipo);
                equiposDashboard.add(equipoDto);
            }

            return ResponseEntity.ok().body(equiposDashboard);
        } catch(SigemaException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().body("Ha ocurrido un error, vuelva a intentarlo");
        }
    }


    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'BRIGADA', 'ADMINISTRADOR_UNIDAD')")
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Equipo equipo) {
        try {
            String rol = jwtUtils.extractRol(getToken());
            EquipoActas equipoActas = equiposService.Crear(equipo);
            TramiteDTO tramite= new TramiteDTO();
            tramite.setIdEquipo(equipoActas.getEquipo().getId());

            tramite.setTexto("Tramite creado automaticamente al recibir un equipo como alta.");
            tramite.setTipoTramite(TipoTramite.AltaEquipo);
            tramite.setIdUnidadDestino(equipoActas.getEquipo().getUnidad().getId());

            Long idUnidad = jwtUtils.extractIdUnidad(getToken());
            if(idUnidad!=null){
                tramite.setIdUnidadOrigen(idUnidad);
            }else {
                idUnidad = unidadService.obtenerGranUnidad().getId();
                tramite.setIdUnidadOrigen(idUnidad);
            }

            Long idUsuario = jwtUtils.extractIdUsuario(getToken());

            Tramite tramiteCreado = tramiteService.Crear(tramite, idUsuario);

            if(Objects.equals(rol, "ROLE_ADMINISTRADOR") || Objects.equals(rol, "ROLE_BRIGADA")) {
                tramiteService.CambiarEstado(tramiteCreado.getId(), EstadoTramite.Aprobado, idUsuario);
            }

            return ResponseEntity.ok().body(equipoActas.getActas());
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
            Equipo equipo = equiposService.ObtenerPorId(id);
            Long idUnidadDestino = 0L;
            String rol = jwtUtils.extractRol(getToken());
            EstadoTramite estadoTramite = EstadoTramite.Iniciado;
            EquipoActas equipoActas = null;

            if(Objects.equals(rol, "ROLE_ADMINISTRADOR") || Objects.equals(rol, "ROLE_BRIGADA")){
                idUnidadDestino = equipo.getUnidad().getId();
                estadoTramite = EstadoTramite.Aprobado;
                equipoActas = equiposService.Eliminar(id);
            }else{
                idUnidadDestino = unidadService.obtenerGranUnidad().getId();
            }

            TramiteDTO tramiteBaja = new TramiteDTO();
            tramiteBaja.setIdEquipo(equipo.getId());
            tramiteBaja.setTexto("Tramite creado automaticamente para dar de baja un equipo.");
            tramiteBaja.setTipoTramite(TipoTramite.BajaEquipo);
            tramiteBaja.setIdUnidadDestino(idUnidadDestino);

            Long idUnidad = jwtUtils.extractIdUnidad(getToken());
            if(idUnidad!=null){
                tramiteBaja.setIdUnidadOrigen(idUnidad);
            }else {
                idUnidad = unidadService.obtenerGranUnidad().getId();
                tramiteBaja.setIdUnidadOrigen(idUnidad);
            }

            Long idUsuario = jwtUtils.extractIdUsuario(getToken());
            Tramite tramite = tramiteService.Crear(tramiteBaja, idUsuario);

            if(estadoTramite == EstadoTramite.Aprobado) {
                tramiteService.CambiarEstado(tramite.getId(), estadoTramite, idUsuario);
            }

            if(equipoActas == null){
                equipoActas = new EquipoActas();
            }

            return ResponseEntity.ok().body(equipoActas.getActas());
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
            Equipo equipo = equiposService.ObtenerPorId(id);

            return ResponseEntity.ok().body(equipo);
        } catch(SigemaException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().body("Ha ocurrido un error, vuelva a intentarlo");
        }
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'BRIGADA','UNIDAD', 'ADMINISTRADOR_UNIDAD')")
    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@PathVariable Long id, @RequestBody Equipo equipo) {
        try {
            Equipo equipoOriginal = equiposService.ObtenerPorId(id);

            if(equipoOriginal == null){
                return ResponseEntity.notFound().build();
            }

            Long idUnidadOriginal = equipoOriginal.getUnidad().getId();

            if(!Objects.equals(idUnidadOriginal, equipo.getIdUnidad())){

                String rol = jwtUtils.extractRol(getToken());

                if(!Objects.equals(rol, "ROLE_ADMINISTRADOR") && !Objects.equals(rol, "ROLE_BRIGADA")){
                    throw new SigemaException("No tiene permiso para editar la unidad a la que pertence el equipo");
                }

                TramiteDTO tramiteBaja = new TramiteDTO();
                tramiteBaja.setIdEquipo(equipo.getId());
                tramiteBaja.setTexto("Tramite creado automaticamente al transferir de unidad el equipo.");
                tramiteBaja.setTipoTramite(TipoTramite.BajaEquipo);
                tramiteBaja.setIdUnidadDestino(idUnidadOriginal);

                Long idUnidad = jwtUtils.extractIdUnidad(getToken());
                if(idUnidad!=null){
                    tramiteBaja.setIdUnidadOrigen(idUnidad);
                }else {
                    idUnidad = unidadService.obtenerGranUnidad().getId();
                    tramiteBaja.setIdUnidadOrigen(idUnidad);
                }

                TramiteDTO tramiteAlta = new TramiteDTO();
                tramiteAlta.setIdEquipo(equipo.getId());
                tramiteAlta.setTexto("Tramite creado automaticamente al transferir de unidad el equipo.");
                tramiteAlta.setTipoTramite(TipoTramite.AltaEquipo);
                tramiteAlta.setIdUnidadDestino(equipo.getIdUnidad());
                tramiteAlta.setIdUnidadOrigen(idUnidad);

                Long idUsuario = jwtUtils.extractIdUsuario(getToken());
                Tramite tramiteBajaCreado = tramiteService.Crear(tramiteBaja, idUsuario);
                Tramite tramiteAltaCreado =tramiteService.Crear(tramiteAlta, idUsuario);
                tramiteService.CambiarEstado(tramiteBajaCreado.getId(), EstadoTramite.Aprobado, idUsuario);
                tramiteService.CambiarEstado(tramiteAltaCreado.getId(), EstadoTramite.Aprobado, idUsuario);
            }

            EquipoActas equipoActas = equiposService.Editar(id, equipo);

            return ResponseEntity.ok().body(equipoActas.actas);
        } catch(SigemaException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().body("Ha ocurrido un error, vuelva a intentarlo");
        }
    }

    @GetMapping("reporteIndicadoresGestion")
    public void exportEquiposExcel(HttpServletResponse response) throws SigemaException {
        Long idUnidad = jwtUtils.extractIdUnidad(getToken());
        String rol = jwtUtils.extractRol(getToken());

        if(Objects.equals(rol, "ROLE_ADMINISTRADOR") || Objects.equals(rol, "ROLE_BRIGADA")){
            idUnidad = null;
        }

        equiposService.GenerarExcelIndicadoresGestion(response, idUnidad);
    }

    @GetMapping("reporteInformePrevisiones")
    public void reporteInformePrevisiones(HttpServletResponse response) throws SigemaException {
        Long idUnidad = jwtUtils.extractIdUnidad(getToken());
        String rol = jwtUtils.extractRol(getToken());

        if(Objects.equals(rol, "ROLE_ADMINISTRADOR") || Objects.equals(rol, "ROLE_BRIGADA")){
            idUnidad = null;
        }

        equiposService.generarExcelInformeAnioProximo(response, idUnidad);
    }
}