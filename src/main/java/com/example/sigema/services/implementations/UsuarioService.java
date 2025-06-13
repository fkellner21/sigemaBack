package com.example.sigema.services.implementations;

import com.example.sigema.models.Usuario;
import com.example.sigema.repositories.IRepositoryUsuario;
import com.example.sigema.services.IUsuarioService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService implements IUsuarioService {

    private IRepositoryUsuario repositorio;

    public UsuarioService(IRepositoryUsuario repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    public List<Usuario> obtenerTodos() throws Exception {

        return repositorio.findAll();
    }

    @Override
    public Usuario Crear(Usuario usuario) throws Exception {
        usuario.validar();
        return repositorio.save(usuario);
    }

    @Override
    public void Eliminar(Long id) throws Exception {
        repositorio.deleteById(id);
    }

    @Override
    public Usuario ObtenerPorId(Long id) throws Exception {
        Usuario usuario = repositorio.findById(id).get();
        if (usuario == null) {
            throw new Exception("No existe el usuario con el id " + id);
        }
        return usuario;
    }

    @Override
    public Usuario Editar(Long id, Usuario usuario) throws Exception {
        usuario.validar();

        Usuario usuarioAModificar = repositorio.findById(id).get();
        if (usuarioAModificar == null) {
            throw new Exception("No existe el usuario con el id " + id);
        }

        usuarioAModificar.setNombreCompleto(usuario.getNombreCompleto());
        usuarioAModificar.setPassword(usuario.getPassword());
        usuarioAModificar.setIdGrado(usuario.getIdGrado());
        usuarioAModificar.setIdUnidad(usuario.getIdUnidad());
        usuarioAModificar.setRol(usuario.getRol());
        usuarioAModificar.getIdUnidad();
        return repositorio.save(usuarioAModificar);
    }
}
