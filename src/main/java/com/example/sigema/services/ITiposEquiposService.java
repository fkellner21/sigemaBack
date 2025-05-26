package com.example.sigema.services;

import com.example.sigema.models.TipoEquipo;

import java.util.List;
import java.util.Optional;

public interface ITiposEquiposService {
    TipoEquipo Crear(TipoEquipo tipoEquipo) throws Exception;

    TipoEquipo Editar(Long id, TipoEquipo tipoEquipo) throws Exception;

    Optional<TipoEquipo> ObtenerPorId(Long id);

    List<TipoEquipo> ObtenerTodos(boolean soloActivos);
}
