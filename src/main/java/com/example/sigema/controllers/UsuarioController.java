package com.example.sigema.controllers;

import com.example.sigema.models.Tramite;
import com.example.sigema.models.TramiteDTO;
import com.example.sigema.models.Usuario;
import com.example.sigema.models.enums.EstadoTramite;
import com.example.sigema.models.enums.Rol;
import com.example.sigema.models.enums.TipoTramite;
import com.example.sigema.services.ITramitesService;
import com.example.sigema.services.IUnidadService;
import com.example.sigema.services.IUsuarioService;
import com.example.sigema.utilidades.JwtUtils;
import com.example.sigema.utilidades.SigemaException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private HttpServletRequest request;
    @Autowired
    private IUnidadService unidadService;
    @Autowired
    private ITramitesService tramiteService;

    private final IUsuarioService usuarioService;

    private final JwtUtils jwtUtils;

    @Autowired
    public UsuarioController(IUsuarioService usuarioService, JwtUtils jwtUtils) {
        this.usuarioService = usuarioService;
        this.jwtUtils = jwtUtils;
    }

    //Si es admin, crea a todos
    //Si es brigada, solo crea a los usuarios de las unidades o Brigada
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'BRIGADA', 'UNIDAD', 'ADMINISTRADOR_UNIDAD')")
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Usuario usuario) {
        try {

            Long idUnidadDestino = 0L;
            String rol = jwtUtils.extractRol(getToken());
            EstadoTramite estadoTramite = EstadoTramite.Iniciado;
            String respuesta="";
            Long idUnidadUsuarioCreador = jwtUtils.extractIdUnidad(getToken());

            if(Objects.equals(rol, "ROLE_ADMINISTRADOR") || Objects.equals(rol, "ROLE_BRIGADA")){
                if(Objects.equals(rol, "ROLE_BRIGADA")&&usuario.getRol()== Rol.ADMINISTRADOR){
                    throw new SigemaException("No tiene permisos para crear un usuario Administrador.");
                }
                idUnidadDestino = usuario.getIdUnidad();
                estadoTramite = EstadoTramite.Aprobado;
                respuesta="Usuario creado con éxito.";
            }else{
                idUnidadDestino = unidadService.obtenerGranUnidad().getId();
                respuesta="Tramite para dar de alta el usuario creado con éxito.";
                if(!Objects.equals(usuario.getUnidad().getId(), idUnidadUsuarioCreador)){
                    throw new SigemaException("Solo puede crear usuarios de su Unidad.");
                }
            }

            TramiteDTO tramiteAlta = new TramiteDTO();
            tramiteAlta.setTexto("Tramite creado automaticamente para dar de alta un usuario, contraseña 123.");
            tramiteAlta.setTipoTramite(TipoTramite.AltaUsuario);
            tramiteAlta.setIdUnidadDestino(idUnidadDestino);
            tramiteAlta.setCedulaUsuarioSolicitado(usuario.getCedula());
            tramiteAlta.setIdGradoUsuarioSolicitado(usuario.getIdGrado());
            tramiteAlta.setTelefonoUsuarioSolicitado(usuario.getTelefono());
            tramiteAlta.setNombreCompletoUsuarioSolicitado(usuario.getNombreCompleto());

            Long idUnidad = jwtUtils.extractIdUnidad(getToken());
            if(idUnidad!=null){
                tramiteAlta.setIdUnidadOrigen(idUnidad);
            }else {
                idUnidad = unidadService.obtenerGranUnidad().getId();
                tramiteAlta.setIdUnidadOrigen(idUnidad);
            }

            Long idUsuario = jwtUtils.extractIdUsuario(getToken());
            Tramite tramite = tramiteService.Crear(tramiteAlta, idUsuario);

            if(estadoTramite == EstadoTramite.Aprobado) {
                tramiteService.CambiarEstado(tramite.getId(),estadoTramite,idUsuario);
            }
            return ResponseEntity.ok(respuesta);
        } catch(SigemaException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Ha ocurrido un error, vuelva a intentarlo");
        }
    }

    //Si es admin, edita a todos
    //Si es brigada, solo puede editarse el y a los usuarios de las unidades
    //Si es unidad solo se edita el mismo
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'BRIGADA', 'UNIDAD', 'ADMINISTRADOR_UNIDAD')")
    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@PathVariable Long id, @RequestBody Usuario usuarioActualizado) {
        try {
            String rol = jwtUtils.extractRol(getToken());
            Long idUsuario = jwtUtils.extractIdUsuario(getToken());

            if(!Objects.equals(rol, "ROLE_ADMINISTRADOR") && usuarioActualizado.getRol()== Rol.ADMINISTRADOR){
                throw new SigemaException("No tiene permisos para editar un usuario Administrador.");
            }
            if(Objects.equals(rol, "ROLE_BRIGADA")&& usuarioActualizado.getRol()!= Rol.UNIDAD && usuarioActualizado.getRol()!= Rol.ADMINISTRADOR_UNIDAD){
                if(!Objects.equals(idUsuario, id)) throw new SigemaException("No tiene permisos para editar el usuario.");
            }
            if(Objects.equals(rol, "ROLE_UNIDAD") || Objects.equals(rol, "ROLE_ADMINISTRADOR_UNIDAD")){
                if(!Objects.equals(idUsuario, usuarioActualizado.getId())){
                    throw new SigemaException("No tiene permisos para editar el usuario.");
                }
            }
            Usuario actualizado = usuarioService.Editar(id, usuarioActualizado);
            return ResponseEntity.ok(actualizado);
        } catch(SigemaException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Ha ocurrido un error, vuelva a intentarlo");
        }
    }

    //Si es admin, elimina a todos
    //Si es brigada, solo puede eliminar a los usuarios de las unidades o Brigada
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'BRIGADA', 'UNIDAD', 'ADMINISTRADOR_UNIDAD')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            Usuario usuario = usuarioService.ObtenerPorId(id);
            Long idUnidadDestino = 0L;
            String rol = jwtUtils.extractRol(getToken());
            EstadoTramite estadoTramite = EstadoTramite.Iniciado;
            String respuesta="";

            if(Objects.equals(rol, "ROLE_ADMINISTRADOR") || Objects.equals(rol, "ROLE_BRIGADA")){
                if(Objects.equals(rol, "ROLE_BRIGADA")&&usuario.getRol()== Rol.ADMINISTRADOR){
                    throw new SigemaException("No tiene permisos para eliminar un usuario Administrador.");
                }
                idUnidadDestino = usuario.getUnidad().getId();
                estadoTramite = EstadoTramite.Aprobado;
                respuesta="Usuario eliminado con éxito.";
            }else{
                idUnidadDestino = unidadService.obtenerGranUnidad().getId();
                respuesta="Tramite para dar de baja el usuario creado con éxito.";
            }

            TramiteDTO tramiteBaja = new TramiteDTO();
            tramiteBaja.setIdUsuarioBaja(usuario.getId());
            tramiteBaja.setTexto("Tramite creado automaticamente para dar de baja un usuario.");
            tramiteBaja.setTipoTramite(TipoTramite.BajaUsuario);
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

            return ResponseEntity.ok().body(respuesta);

        } catch(SigemaException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Ha ocurrido un error, vuelva a intentarlo");
        }
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'BRIGADA', 'UNIDAD', 'ADMINISTRADOR_UNIDAD')")
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerUsuarioPorId(@PathVariable Long id) {
        try {
            Usuario usuario = usuarioService.ObtenerPorId(id);
            return ResponseEntity.ok(usuario);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'BRIGADA', 'UNIDAD', 'ADMINISTRADOR_UNIDAD')")
    @GetMapping
    public ResponseEntity<List<Usuario>> obtenerUsuarios() {
        try {
            List<Usuario> usuarios = usuarioService.obtenerTodos();
            return ResponseEntity.ok(usuarios);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    public String getToken() {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        throw new RuntimeException("No se encontró el token en el header");
    }
}