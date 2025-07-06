package com.example.sigema.services;

import com.example.sigema.models.Equipo;

import java.util.List;
import java.util.Optional;

public interface IEquipoService {

    List<Equipo> obtenerTodos(Long idUnidad) throws Exception;

    Equipo Crear(Equipo equipo) throws Exception;

    void Eliminar(Long id) throws Exception;

    Equipo ObtenerPorId(Long id) throws Exception;

    Equipo Editar(Long id, Equipo equipo) throws Exception;

    List<Equipo> obtenerEquiposPorIdModelo(Long idModelo, Long idUnidad);
}
