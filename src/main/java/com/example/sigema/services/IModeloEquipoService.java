package com.example.sigema.services;

import com.example.sigema.models.ModeloEquipo;

import java.util.List;
import java.util.Optional;


public interface IModeloEquipoService {

    ModeloEquipo Crear(ModeloEquipo modeloEquipo) throws Exception;

    ModeloEquipo Editar(Long id, ModeloEquipo modeloEquipo) throws Exception;

    Optional<ModeloEquipo> ObtenerPorId(Long id) throws Exception;

    List<ModeloEquipo> ObtenerTodos();

}
