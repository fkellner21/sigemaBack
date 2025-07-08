package com.example.sigema.services;


import com.example.sigema.models.Marca;

import java.util.List;
import java.util.Optional;

public interface IMarcaService {
    List<Marca> ObtenerTodos() throws Exception;

    Marca Crear(Marca marca) throws Exception;

    Optional<Marca> ObtenerPorId(Long id) throws Exception;

    Marca Editar(Long id, Marca marca) throws Exception;
}
