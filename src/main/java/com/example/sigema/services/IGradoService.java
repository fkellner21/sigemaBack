package com.example.sigema.services;

import com.example.sigema.models.Grado;

import java.util.List;

public interface IGradoService {
    List<Grado> obtenerTodos() throws Exception;

    Grado Crear(Grado grado) throws Exception;

    void Eliminar(Long id) throws Exception;

    Grado ObtenerPorId(Long id) throws Exception;

    Grado Editar(Long id, Grado grado) throws Exception;
}
