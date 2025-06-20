package com.example.sigema.services.implementations;

import com.example.sigema.configurations.SecurityConfig;
import com.example.sigema.models.Usuario;
import com.example.sigema.models.Unidad;
import com.example.sigema.models.Grado;
import com.example.sigema.repositories.IRepositoryUsuario;
import com.example.sigema.services.IGradoService;
import com.example.sigema.services.IUnidadService;
import com.example.sigema.services.IUsuarioService;
import com.example.sigema.utilidades.SigemaException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService implements IUsuarioService {

    private IRepositoryUsuario repositorio;
    private IUnidadService unidadService;
    private IGradoService gradoService;
    private SecurityConfig securityConfig;

    public UsuarioService(IRepositoryUsuario repositorio, IGradoService gradoService, IUnidadService unidadService, SecurityConfig securityConfig) {
        this.repositorio = repositorio;
        this.gradoService = gradoService;
        this.unidadService = unidadService;
        this.securityConfig = securityConfig;
    }

    @Override
    public List<Usuario> obtenerTodos() throws Exception {
        return repositorio.findAll();
    }

    @Override
    public Usuario Crear(Usuario usuario) throws Exception {
        usuario.validar();

        Unidad unidad = unidadService.ObtenerPorId(usuario.getIdUnidad()).orElse(null);
        Grado grado = gradoService.ObtenerPorId(usuario.getIdGrado());

        if(usuario.getPassword() == null || usuario.getPassword().isEmpty()){
            throw new SigemaException("Debe ingresar una contraseña");
        }

        usuario.setUnidad(unidad);
        usuario.setGrado(grado);
        usuario.setPassword(securityConfig.passwordEncoder().encode(usuario.getPassword()));

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

        Unidad unidad = unidadService.ObtenerPorId(usuario.getIdUnidad()).orElse(null);
        Grado grado = gradoService.ObtenerPorId(usuario.getIdGrado());

        usuarioAModificar.setUnidad(unidad);
        usuarioAModificar.setGrado(grado);
        usuarioAModificar.setNombreCompleto(usuario.getNombreCompleto());
        usuarioAModificar.setRol(usuario.getRol());

        if(usuario.getPassword() != null && !usuario.getPassword().isEmpty()){
            usuarioAModificar.setPassword(securityConfig.passwordEncoder().encode(usuario.getPassword()));
        }

        return repositorio.save(usuarioAModificar);
    }
}
