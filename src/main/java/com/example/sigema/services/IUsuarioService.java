package com.example.sigema.services;

import com.example.sigema.models.Usuario;

import java.util.List;

public interface IUsuarioService {
    public List<Usuario> obtenerTodos() throws Exception;

    public Usuario Crear(Usuario usuario) throws Exception;

    public void Eliminar(Long id) throws Exception;

    public Usuario ObtenerPorId(Long id) throws Exception;

    public Usuario Editar(Long id, Usuario usuario) throws Exception;
}
