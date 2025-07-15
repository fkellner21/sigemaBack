package com.example.sigema.services;

import com.example.sigema.models.Usuario;

import java.util.List;

public interface IUsuarioService {
    List<Usuario> obtenerTodos() throws Exception;

    Usuario Crear(Usuario usuario) throws Exception;

    void Eliminar(Long id) throws Exception;

    Usuario ObtenerPorId(Long id) throws Exception;

    Usuario Editar(Long id, Usuario usuario) throws Exception;

    boolean ExistePorCedula(String cedula) throws Exception;
    List<Usuario> obtenerTodosPorIdUnidad(Long idUnidad);
}
