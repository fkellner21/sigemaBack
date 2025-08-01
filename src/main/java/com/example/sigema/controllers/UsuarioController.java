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
//@CrossOrigin(origins = "*")
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
            Rol rolSolicitado = usuario.getRol();

            if(usuarioService.ExistePorCedula(usuario.getCedula())){
                throw new SigemaException("Ya existe usuario con esa CI");
            }

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
                if(!Objects.equals(usuario.getIdUnidad(), idUnidadUsuarioCreador)){
                    throw new SigemaException("Solo puede crear usuarios de su Unidad.");
                }
                rolSolicitado=Rol.UNIDAD;
            }

            TramiteDTO tramiteAlta = new TramiteDTO();
            tramiteAlta.setTexto("Tramite creado automaticamente para dar de alta al usuario:" +
                    "Nombre: "+usuario.getNombreCompleto()+", CI: "+usuario.getCedula()+" ,contraseña: 123.");
            tramiteAlta.setTipoTramite(TipoTramite.AltaUsuario);
            tramiteAlta.setIdUnidadDestino(idUnidadDestino);
            tramiteAlta.setCedulaUsuarioSolicitado(usuario.getCedula());
            tramiteAlta.setIdGradoUsuarioSolicitado(usuario.getIdGrado());
            tramiteAlta.setTelefonoUsuarioSolicitado(usuario.getTelefono());
            tramiteAlta.setNombreCompletoUsuarioSolicitado(usuario.getNombreCompleto());
            tramiteAlta.setRolSolicitado(rolSolicitado);
            tramiteAlta.setIdUnidadUsuarioSolicitado(usuario.getIdUnidad());

            Long idUnidad = jwtUtils.extractIdUnidad(getToken());
            if(idUnidad!=null){
                tramiteAlta.setIdUnidadOrigen(idUnidad);
            }else {
                idUnidad = unidadService.obtenerGranUnidad().getId();
                tramiteAlta.setIdUnidadOrigen(idUnidad);
            }

            Long idUsuario = jwtUtils.extractIdUsuario(getToken());
            Tramite tramite = tramiteService.Crear(tramiteAlta, idUsuario);

            if(estadoTramite == EstadoTramite.Aprobado) { //se crea el usuario
                tramiteService.CambiarEstado(tramite.getId(),estadoTramite,idUsuario);
            }
            return ResponseEntity.ok().build();
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
                if(usuario.getUnidad()!=null){
                idUnidadDestino = usuario.getUnidad().getId();
                }
                estadoTramite = EstadoTramite.Aprobado;

            }else{
                idUnidadDestino = unidadService.obtenerGranUnidad().getId();

            }

            TramiteDTO tramiteBaja = new TramiteDTO();
            tramiteBaja.setIdUsuarioBaja(usuario.getId());
            tramiteBaja.setTexto("Tramite creado automaticamente para dar de baja al usuario:" +
                    "Nombre: "+usuario.getNombreCompleto()+", CI: "+usuario.getCedula()+" ,contraseña: 123.");
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

            return ResponseEntity.ok().build();

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