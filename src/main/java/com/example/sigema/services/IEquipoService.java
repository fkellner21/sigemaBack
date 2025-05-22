package com.example.sigema.services;

import com.example.sigema.models.Equipo;

import java.util.List;
import java.util.Optional;

public interface IEquipoService {

    public List<Equipo> obtenerTodos() throws Exception;
    public void Crear(Equipo equipo) throws Exception;
    public void Eliminar(Long id) throws Exception;
    public Optional<Equipo> obtenerPorId(Long id) throws Exception;
    public void Editar(Equipo equipo) throws Exception;

}
