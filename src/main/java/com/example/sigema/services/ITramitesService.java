package com.example.sigema.services;

import com.example.sigema.models.*;
import com.example.sigema.models.enums.EstadoTramite;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ITramitesService {

    List<Tramite> ObtenerTodosPorFechas(Long idUnidad, Date desde, Date hasta) throws Exception;

    List<Tramite> ObtenerTodos(Long idUnidad) throws Exception;

    Tramite Crear(TramiteDTO t, Long idUsuario) throws Exception;

    Optional<Tramite> ObtenerPorId(Long id, Usuario usuario) throws Exception;

    Tramite Editar(Long id, TramiteDTO t, Long idUsuario) throws Exception;
    void Eliminar(Long id) throws Exception;
    Actuacion CrearActuacion(Long idTramite, Actuacion actuacion, Long idUsuario) throws Exception;

    EquipoActas CambiarEstado(Long id, EstadoTramite estado, Long idUsuario) throws Exception;
}
