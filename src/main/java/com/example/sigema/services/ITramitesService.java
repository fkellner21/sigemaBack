package com.example.sigema.services;

import com.example.sigema.models.Actuacion;
import com.example.sigema.models.Tramite;
import com.example.sigema.models.TramiteDTO;
import com.example.sigema.models.enums.EstadoTramite;

import java.util.List;
import java.util.Optional;

public interface ITramitesService {
    public List<Tramite> ObtenerTodos(Long idUnidad) throws Exception;

    public Tramite Crear(TramiteDTO t, Long idUsuario) throws Exception;

    public Optional<Tramite> ObtenerPorId(Long id) throws Exception;

    public Tramite Editar(Long id, TramiteDTO t, Long idUsuario) throws Exception;
    public void Eliminar(Long id) throws Exception;
    public Actuacion CrearActuacion(Long idTramite, Actuacion actuacion, Long idUsuario) throws Exception;

    public Tramite CambiarEstado(Long id, EstadoTramite estado, Long idUsuario) throws Exception;
}
