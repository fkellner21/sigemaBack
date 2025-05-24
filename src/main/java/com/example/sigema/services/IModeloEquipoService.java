package com.example.sigema.services;

import com.example.sigema.models.ModeloEquipo;

import java.util.List;
import java.util.Optional;


public interface IModeloEquipoService {

        public ModeloEquipo Crear(ModeloEquipo modeloEquipo) throws Exception;

        public ModeloEquipo Editar(Long id, ModeloEquipo modeloEquipo) throws Exception;

        public Optional<ModeloEquipo> ObtenerPorId(Long id) throws Exception;

        public List<ModeloEquipo> ObtenerTodos();
}
