package com.example.sigema.services;

import com.example.sigema.models.Equipo;
import com.example.sigema.models.Unidad;
import com.example.sigema.utilidades.SigemaException;

import java.util.List;
import java.util.Optional;

public interface IUnidadService {

    List<Unidad> obtenerTodos() throws Exception;

    Unidad Crear(Unidad unidad) throws Exception;

    void Eliminar(Long id) throws Exception;

    Optional<Unidad> ObtenerPorId(Long id) throws Exception;

    Unidad Editar(Long id, Unidad unidad) throws Exception;

    Unidad obtenerGranUnidad() throws SigemaException;
}
