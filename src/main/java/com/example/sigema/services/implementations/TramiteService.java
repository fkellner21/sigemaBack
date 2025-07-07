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
import java.util.Date;
import java.util.List;
import java.util.Optional;

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
    public Tramite Crear(TramiteDTO t, Long idUsuario) throws Exception {
        Tramite tramite = mapearTramiteDesdeDTO(t,idUsuario, new Tramite(), true);

        EstadosHistoricoTramite nuevoEstado = new EstadosHistoricoTramite();
        nuevoEstado.setEstado(tramite.getEstado());
        nuevoEstado.setTramite(tramite);
        nuevoEstado.setFecha(tramite.getFechaInicio());
        nuevoEstado.setUsuario(tramite.getUsuario());

        tramite.getHistorico().add(nuevoEstado);
        tramite = tramitesRepository.save(tramite);

        return tramite;
    }

    @Override
    public Optional<Tramite> ObtenerPorId(Long id, Usuario quienAbre) {
        Tramite tramite = tramitesRepository.findById(id).orElse(null);
        if(tramite!=null && quienAbre != null) tramite.actualizarEstado(quienAbre);
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

        if((tramite.getEstado() == EstadoTramite.Aprobado || tramite.getEstado() == EstadoTramite.Rechazado) && estado == EstadoTramite.EnTramite){
    //        if(usuario.getRol() != Rol.ADMINISTRADOR && usuario.getRol() != Rol.BRIGADA){
                throw new SigemaException("No se puede re abrir un trámite");
     //       }
        }

        EstadosHistoricoTramite nuevoEstado = new EstadosHistoricoTramite();
        nuevoEstado.setTramite(tramite);
        nuevoEstado.setEstado(estado);
        nuevoEstado.setFecha(Date.from(Instant.now()));
        nuevoEstado.setUsuario(usuario);

        tramite.getHistorico().add(nuevoEstado);
        tramite.setEstado(estado);

        tramite = tramitesRepository.save(tramite);

        if(tramite.getEstado() == EstadoTramite.Aprobado && tramite.getTipoTramite() == TipoTramite.BajaEquipo){
            equipoService.Eliminar(tramite.getEquipo().getId());
        }

        if(tramite.getEstado()==EstadoTramite.Aprobado && tramite.getTipoTramite() == TipoTramite.BajaUsuario){
            usuarioService.Eliminar(tramite.getIdUsuarioBajaSolicitada());
        }

        if(tramite.getEstado()==EstadoTramite.Aprobado && tramite.getTipoTramite() == TipoTramite.AltaUsuario){

            Usuario nuevo = new Usuario();
            nuevo.setNombreCompleto(tramite.getNombreCompletoUsuarioSolicitado());
            nuevo.setPassword("123");
            nuevo.setCedula(tramite.getCedulaUsuarioSolicitado());
            nuevo.setIdGrado(tramite.getIdGradoUsuarioSolicitado());
            nuevo.setIdUnidad(tramite.getUnidadOrigen().getId());
            nuevo.setTelefono(tramite.getTelefonoUsuarioSolicitado());
            nuevo.setRol(Rol.UNIDAD);

            usuarioService.Crear(nuevo);
        }

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

        if (tramite.getTipoTramite() == TipoTramite.BajaEquipo || tramite.getTipoTramite() == TipoTramite.SolicitudRepuesto) {
            if (t.getIdEquipo() == null || t.getIdEquipo() <= 0) {
                throw new SigemaException("Debe ingresar un modelo de equipo");
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
        }
        if(tramite.getTipoTramite()==TipoTramite.BajaUsuario){
            tramite.setIdUsuarioBajaSolicitada(t.getIdUsuarioBaja());
        }

        return tramite;
    }
}