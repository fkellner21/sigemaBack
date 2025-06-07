package com.example.sigema.services;

import com.example.sigema.models.Equipo;
import com.example.sigema.models.Unidad;

import java.util.List;
import java.util.Optional;

public interface IUnidadService {

    public List<Unidad> obtenerTodos() throws Exception;

    public Unidad Crear(Unidad unidad) throws Exception;

    public void Eliminar(Long id) throws Exception;

    public Optional<Unidad> ObtenerPorId(Long id) throws Exception;

    public Unidad Editar(Long id, Unidad unidad) throws Exception;

}
