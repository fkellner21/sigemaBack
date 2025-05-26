package com.example.sigema.services;


import com.example.sigema.models.Marca;

import java.util.List;
import java.util.Optional;

public interface IMarcaService {
    public List<Marca> ObtenerTodos() throws Exception;

    public Marca Crear(Marca marca) throws Exception;

    public Optional<Marca> ObtenerPorId(Long id) throws Exception;

    public Marca Editar(Long id, Marca marca) throws Exception;
}
