package com.example.sigema.services;

import com.example.sigema.models.Notificacion;

import java.util.List;

public interface INotificacionesService {
    void Eliminar(Long id) throws Exception;
    Notificacion ObtenerPorId(Long id) throws Exception;
    List<Notificacion> obtenerPorIdUsuario(Long idUsuario) throws Exception;
    List<Notificacion> obtenerPorIdUsuarioAndIdTramite(Long idUsuario, Long idTramite) throws Exception;
    void Crear(Notificacion notificacion) throws Exception;
}