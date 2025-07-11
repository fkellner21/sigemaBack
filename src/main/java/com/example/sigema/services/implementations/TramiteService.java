package com.example.sigema.services.implementations;

import com.example.sigema.models.*;
import com.example.sigema.models.enums.EstadoTramite;
import com.example.sigema.models.enums.Rol;
import com.example.sigema.models.enums.TipoTramite;
import com.example.sigema.repositories.ITramitesRepository;
import com.example.sigema.services.*;
import com.example.sigema.utilidades.SigemaException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;

@Service
public class TramiteService implements ITramitesService {

    private final ITramitesRepository tramitesRepository;
    private final IUnidadService unidadService;
    private final IUsuarioService usuarioService;
    private final IEquipoService equipoService;
    private final IRepuestoService repuestoService;

    public TramiteService (ITramitesRepository tramitesRepository, IUnidadService unidadService, IUsuarioService usuarioService,
                           IEquipoService equipoService, IRepuestoService repuestoService){
        this.tramitesRepository = tramitesRepository;
        this.unidadService = unidadService;
        this.usuarioService = usuarioService;
        this.equipoService = equipoService;
        this.repuestoService = repuestoService;
    }

    @Override
    public List<Tramite> ObtenerTodos(Long idUnidad) throws Exception {
        if(idUnidad == null || idUnidad == 0){
            return tramitesRepository.findAll();
        }

        List<Tramite> tramites = tramitesRepository.findByUnidadOrigen_Id(idUnidad);
        tramites.addAll(tramitesRepository.findByUnidadDestino_Id(idUnidad));
        tramites.addAll(tramitesRepository.findByUnidadDestino_Id(null));

        return tramites.stream().distinct().toList();
    }

    @Override
    public List<Tramite> ObtenerTodosPorFechas(Long idUnidad, Date desde, Date hasta) throws Exception {
        ZoneId zone = ZoneId.of("America/Montevideo");

        // Pasar Date → LocalDate → Date para ignorar la hora
        LocalDate localDesde = desde.toInstant().atZone(zone).toLocalDate();
        LocalDate localHasta = hasta.toInstant().atZone(zone).toLocalDate();

        Date fechaDesde = Date.from(localDesde.atStartOfDay(zone).toInstant());
        Date fechaHasta = Date.from(localHasta.atTime(LocalTime.MAX).atZone(zone).toInstant());

        List<Tramite> tramites;
        if (idUnidad == null || idUnidad == 0) {
            tramites = tramitesRepository.findByFechaInicioBetween(fechaDesde, fechaHasta);
        } else {
            tramites = tramitesRepository.findByUnidadOrigen_IdAndFechaInicioBetween(idUnidad, fechaDesde, fechaHasta);
            tramites.addAll(tramitesRepository.findByUnidadDestino_IdAndFechaInicioBetween(idUnidad, fechaDesde, fechaHasta));
            tramites.addAll(tramitesRepository.findByUnidadDestino_IdAndFechaInicioBetween(null, fechaDesde, fechaHasta));
        }

        return tramites.stream().distinct().toList();
    }

    @Override
    public Tramite Crear(TramiteDTO t, Long idUsuario) throws Exception {

        if(t.getTipoTramite()==TipoTramite.AltaUsuario||t.getTipoTramite()==TipoTramite.BajaUsuario){
            List<Tramite> tramites = tramitesRepository.findAll();
            boolean existen = tramites.stream()
                    .anyMatch(tr -> (tr.getEstado() == EstadoTramite.Iniciado || tr.getEstado() == EstadoTramite.EnTramite)
                            &&(
                            (tr.getCedulaUsuarioSolicitado() != null&& t.getCedulaUsuarioSolicitado()!=null && t.getCedulaUsuarioSolicitado().equals(tr.getCedulaUsuarioSolicitado()))
                                    ||
                            (tr.getIdUsuarioBajaSolicitada()!=null && t.getIdUsuarioBaja()!=null && t.getIdUsuarioBaja().equals(tr.getIdUsuarioBajaSolicitada()))
                            ));
            if(existen){
                throw new SigemaException("Hay tramites pendientes para el usuario solicitado");
            }
        }

        Tramite tramite = mapearTramiteDesdeDTO(t,idUsuario, new Tramite(), true);


        Usuario quienCrea = usuarioService.ObtenerPorId(idUsuario);

        VisualizacionTramite vista = new VisualizacionTramite();
        vista.setDescripcion("Crea");
        vista.setTramite(tramite);
        vista.setFecha(Date.from(Instant.now()));
        vista.setUsuario(quienCrea);
        tramite.getVisualizaciones().add(vista);

        tramite = tramitesRepository.save(tramite);

        return tramite;
    }

    @Override
    public Optional<Tramite> ObtenerPorId(Long id, Usuario quienAbre) {
        Tramite tramite = tramitesRepository.findById(id).orElse(null);
        boolean pasoAenTramite = false;

        if(tramite!=null && quienAbre != null && tramite.getEstado()==EstadoTramite.Iniciado){
            tramite.actualizarEstado(quienAbre);
            tramite = tramitesRepository.save(tramite);
            pasoAenTramite=true;
        }

        if(tramite != null && quienAbre != null) {
            VisualizacionTramite visualizacionExistente = tramite.getVisualizaciones().stream()
                    .filter(x -> x.getUsuario().getId().equals(quienAbre.getId()))
                    .findFirst()
                    .orElse(null);

            VisualizacionTramite visualizacionMasReciente = null;
            if (!tramite.getVisualizaciones().isEmpty()) {
                visualizacionMasReciente = tramite.getVisualizaciones().stream()
                        .max(Comparator.comparing(VisualizacionTramite::getFecha))
                        .orElse(null);
            }
            if(visualizacionExistente == null || //quien abre nunca habia abierto
                    (visualizacionMasReciente!=null &&
                            !Objects.equals(visualizacionMasReciente.getUsuario().getId(), quienAbre.getId()))){
                visualizacionExistente = new VisualizacionTramite();
                visualizacionExistente.setTramite(tramite);
                visualizacionExistente.setUsuario(quienAbre);
                visualizacionExistente.setFecha(Date.from(Instant.now()));
                if(pasoAenTramite){
                    visualizacionExistente.setDescripcion("Vista y cambio a En Tramite");
                }else{
                    visualizacionExistente.setDescripcion("Vista");
                }
                tramite.getVisualizaciones().add(visualizacionExistente);
            }
            tramitesRepository.save(tramite);
        }
        return tramite != null ? Optional.of(tramite) : Optional.empty();
    }


    @Override
    public Tramite Editar(Long id, TramiteDTO t, Long idUsuario) throws Exception {
        Tramite tramite = tramitesRepository.findById(id).orElse(null);

        if(tramite == null){
            throw new SigemaException("El trámite no fue encontrado");
        }
        if (tramite.getEstado()!=EstadoTramite.Iniciado){
            throw new SigemaException("Su trámite ya fue procesado y no se puede editar");
        }

        mapearTramiteDesdeDTO(t,idUsuario, tramite, false);

        tramite = tramitesRepository.save(tramite);

        return tramite;
    }

    @Override
    public void Eliminar(Long id) throws Exception{
        Tramite tramite = tramitesRepository.findById(id).orElse(null);

        if(tramite == null){
            throw new SigemaException("El tramite no fue encontrado");
        }

        tramitesRepository.delete(tramite);
    }

    @Override
    public Actuacion CrearActuacion(Long idTramite, Actuacion actuacion, Long idUsuario) throws Exception {
        Tramite tramite = tramitesRepository.findById(idTramite).orElse(null);

        if(tramite == null){
            throw new SigemaException("El tramite no fue encontrado");
        }

        if(tramite.getEstado()==EstadoTramite.Aprobado || tramite.getEstado()==EstadoTramite.Rechazado){
            throw new SigemaException("Nos se puede crear la actuación porque el trámite ya está resuelto");
        }

        Usuario usuario = usuarioService.ObtenerPorId(idUsuario);

        if(usuario == null){
            throw new SigemaException("El usuario no fue encontrado");
        }

        actuacion.setTramite(tramite);
        actuacion.setUsuario(usuario);

        tramite.getActuaciones().add(actuacion);
        tramitesRepository.save(tramite);

        return actuacion;
    }

    @Override
    public Tramite CambiarEstado(Long id, EstadoTramite estado, Long idUsuario) throws Exception {
        Tramite tramite = tramitesRepository.findById(id).orElse(null);

        if(tramite == null){
            throw new SigemaException("El tramite no fue encontrado");
        }

        if(tramite.getEstado() == estado){
            throw new SigemaException("El tramite ya se encuentra en el estado seleccionado");
        }

        if(idUsuario == null || idUsuario <= 0){
            throw new SigemaException("Debe ingresar un usuario");
        }

        Usuario usuario = usuarioService.ObtenerPorId(idUsuario);

        if(usuario == null){
            throw new SigemaException("El usuario no fue encontrado");
        }

        if((estado == EstadoTramite.Aprobado || tramite.getEstado() == EstadoTramite.Rechazado) && estado == EstadoTramite.EnTramite){
                throw new SigemaException("No se puede re abrir un trámite");
        }

        if(estado == EstadoTramite.Aprobado && tramite.getTipoTramite() == TipoTramite.BajaEquipo){
            equipoService.Eliminar(tramite.getEquipo().getId());
            VisualizacionTramite nueva = new VisualizacionTramite();
            nueva.setTramite(tramite);
            nueva.setUsuario(usuario);
            nueva.setFecha(Date.from(Instant.now()));
            nueva.setDescripcion("Aprueba");
            tramite.getVisualizaciones().add(nueva);
        }

        if(estado == EstadoTramite.Rechazado && tramite.getTipoTramite() == TipoTramite.BajaEquipo){
            VisualizacionTramite nueva = new VisualizacionTramite();
            nueva.setTramite(tramite);
            nueva.setUsuario(usuario);
            nueva.setFecha(Date.from(Instant.now()));
            nueva.setDescripcion("Rechaza");
            tramite.getVisualizaciones().add(nueva);
        }

        if(estado==EstadoTramite.Aprobado && tramite.getTipoTramite() == TipoTramite.BajaUsuario){
            usuarioService.Eliminar(tramite.getIdUsuarioBajaSolicitada());
            VisualizacionTramite nueva = new VisualizacionTramite();
            nueva.setTramite(tramite);
            nueva.setUsuario(usuario);
            nueva.setFecha(Date.from(Instant.now()));
            nueva.setDescripcion("Aprueba");
            tramite.getVisualizaciones().add(nueva);
        }

        if(estado==EstadoTramite.Rechazado && tramite.getTipoTramite() == TipoTramite.BajaUsuario){
            VisualizacionTramite nueva = new VisualizacionTramite();
            nueva.setTramite(tramite);
            nueva.setUsuario(usuario);
            nueva.setFecha(Date.from(Instant.now()));
            nueva.setDescripcion("Rechaza");
            tramite.getVisualizaciones().add(nueva);
        }

        if(estado==EstadoTramite.Aprobado && tramite.getTipoTramite() == TipoTramite.AltaUsuario){

            Usuario nuevo = new Usuario();
            nuevo.setNombreCompleto(tramite.getNombreCompletoUsuarioSolicitado());
            nuevo.setPassword("123");
            nuevo.setCedula(tramite.getCedulaUsuarioSolicitado());
            nuevo.setIdGrado(tramite.getIdGradoUsuarioSolicitado());
            nuevo.setIdUnidad(tramite.getIdUnidadUsuarioSolicitado());
            nuevo.setTelefono(tramite.getTelefonoUsuarioSolicitado());
            nuevo.setRol(tramite.getRolSolicitado());

            usuarioService.Crear(nuevo);

            VisualizacionTramite nueva = new VisualizacionTramite();
            nueva.setTramite(tramite);
            nueva.setUsuario(usuario);
            nueva.setFecha(Date.from(Instant.now()));
            nueva.setDescripcion("Aprueba");
            tramite.getVisualizaciones().add(nueva);
        }

        if(estado==EstadoTramite.Rechazado && tramite.getTipoTramite() == TipoTramite.AltaUsuario){

            VisualizacionTramite nueva = new VisualizacionTramite();
            nueva.setTramite(tramite);
            nueva.setUsuario(usuario);
            nueva.setFecha(Date.from(Instant.now()));
            nueva.setDescripcion("Rechaza");
            tramite.getVisualizaciones().add(nueva);
        }

        if(tramite.getTipoTramite()!=TipoTramite.AltaUsuario&&tramite.getTipoTramite()!=TipoTramite.BajaUsuario&&
        tramite.getTipoTramite()!=TipoTramite.BajaEquipo){
            if(estado==EstadoTramite.Aprobado){
                VisualizacionTramite nueva = new VisualizacionTramite();
                nueva.setTramite(tramite);
                nueva.setUsuario(usuario);
                nueva.setFecha(Date.from(Instant.now()));
                nueva.setDescripcion("Aprueba");
                tramite.getVisualizaciones().add(nueva);
            }
            if(estado==EstadoTramite.Rechazado){
                VisualizacionTramite nueva = new VisualizacionTramite();
                nueva.setTramite(tramite);
                nueva.setUsuario(usuario);
                nueva.setFecha(Date.from(Instant.now()));
                nueva.setDescripcion("Rechaza");
                tramite.getVisualizaciones().add(nueva);
            }
        }

        tramite.setEstado(estado);
        tramite = tramitesRepository.save(tramite);
        return tramite;
    }

    private Tramite mapearTramiteDesdeDTO(TramiteDTO t,Long idUsuario, Tramite tramite, boolean esCreacion) throws Exception {
        if (esCreacion) {
            tramite.setFechaInicio(Date.from(Instant.now()));
            tramite.setEstado(EstadoTramite.Iniciado);
        }

        tramite.setTipoTramite(t.getTipoTramite());
        tramite.setTexto(t.getTexto());
        Usuario usuario = usuarioService.ObtenerPorId(idUsuario);
        if (usuario == null) {
            throw new SigemaException("El usuario no fue encontrado");
        }
        tramite.setUsuario(usuario);

        if (t.getIdUnidadOrigen() == null || t.getIdUnidadOrigen() <= 0) {
            throw new SigemaException("Debe ingresar la unidad de origen");
        }
        Unidad unidadOrigen = unidadService.ObtenerPorId(t.getIdUnidadOrigen()).orElse(null);
        if (unidadOrigen == null) {
            throw new SigemaException("La unidad de origen no fue encontrada");
        }
        tramite.setUnidadOrigen(unidadOrigen);

        if (t.getIdUnidadDestino() != null && t.getIdUnidadDestino() > 0) {
            Unidad unidadDestino = unidadService.ObtenerPorId(t.getIdUnidadDestino()).orElse(null);
            if (unidadDestino == null) {
                throw new SigemaException("La unidad de destino no fue encontrada");
            }
            tramite.setUnidadDestino(unidadDestino);
        }else{
            tramite.setUnidadDestino(null);
        }

        if (tramite.getTipoTramite() == TipoTramite.BajaEquipo||tramite.getTipoTramite()==TipoTramite.AltaEquipo || tramite.getTipoTramite() == TipoTramite.SolicitudRepuesto) {
            if (t.getIdEquipo() == null || t.getIdEquipo() <= 0) {
                throw new SigemaException("Debe ingresar un equipo / modelo de equipo");
            }

            Equipo equipo = equipoService.ObtenerPorId(t.getIdEquipo());
            if (equipo == null) {
                throw new SigemaException("El equipo no fue encontrado");
            }

            tramite.setEquipo(equipo);

            if (tramite.getTipoTramite() == TipoTramite.SolicitudRepuesto) {
                if (t.getIdRepuesto() == null || t.getIdRepuesto() <= 0) {
                    throw new SigemaException("Debe ingresar un repuesto");
                }

                Repuesto repuesto = repuestoService.ObtenerPorId(t.getIdRepuesto()).orElse(null);
                if (repuesto == null) {
                    throw new SigemaException("El repuesto no fue encontrado");
                }

                tramite.setRepuesto(repuesto);
            }
        }
        if(tramite.getTipoTramite()==TipoTramite.AltaUsuario){
            tramite.setCedulaUsuarioSolicitado(t.getCedulaUsuarioSolicitado());
            tramite.setIdGradoUsuarioSolicitado(t.getIdGradoUsuarioSolicitado());
            tramite.setNombreCompletoUsuarioSolicitado(t.getNombreCompletoUsuarioSolicitado());
            tramite.setTelefonoUsuarioSolicitado(t.getTelefonoUsuarioSolicitado());
            tramite.setRolSolicitado(t.getRolSolicitado());
            tramite.setIdUnidadUsuarioSolicitado(t.getIdUnidadUsuarioSolicitado());
        }
        if(tramite.getTipoTramite()==TipoTramite.BajaUsuario){
            tramite.setIdUsuarioBajaSolicitada(t.getIdUsuarioBaja());
        }

        return tramite;
    }
}