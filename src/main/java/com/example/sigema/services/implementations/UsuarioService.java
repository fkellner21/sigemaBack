package com.example.sigema.services.implementations;

import com.example.sigema.SecurityConfig;
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
import java.util.UUID;

@Service
public class UsuarioService implements IUsuarioService {

    private final IRepositoryUsuario repositorio;
    private final IUnidadService unidadService;
    private final IGradoService gradoService;
    private final SecurityConfig securityConfig;

    public UsuarioService(IRepositoryUsuario repositorio, IGradoService gradoService, IUnidadService unidadService, SecurityConfig securityConfig) {
        this.repositorio = repositorio;
        this.gradoService = gradoService;
        this.unidadService = unidadService;
        this.securityConfig = securityConfig;
    }

    @Override
    public List<Usuario> obtenerTodos() throws Exception {
        return repositorio.findAllByActivoTrue();
    }

    @Override
    public Usuario Crear(Usuario usuario) throws Exception {
        usuario.validar();
        usuario.setCedula(usuario.getCedula().replaceAll("[./-]", ""));
        usuario.setActivo(true);

        if(usuario.getIdUnidad() != null) {
            Unidad unidad = unidadService.ObtenerPorId(usuario.getIdUnidad()).orElse(null);
            usuario.setUnidad(unidad);
        }else{
            usuario.setUnidad(null);
        }

        Grado grado = gradoService.ObtenerPorId(usuario.getIdGrado());

        if(usuario.getPassword() == null || usuario.getPassword().isEmpty()){
            throw new SigemaException("Debe ingresar una contraseña");
        }

        usuario.setGrado(grado);
        usuario.setPassword(securityConfig.passwordEncoder().encode(usuario.getPassword()));

        return repositorio.save(usuario);
    }

    @Override
    public void Eliminar(Long id) throws Exception {
        try {
        Usuario usuario = ObtenerPorId(id);

        if(usuario.getGrado() != null){
            usuario.setIdGrado(usuario.getGrado().getId());
        }

        if(usuario.getUnidad() != null){
            usuario.setIdUnidad(usuario.getUnidad().getId());
        }

        usuario.setActivo(false);
        Editar(id, usuario);
        } catch (Exception e) {
            throw new SigemaException("Error al intentar eliminar el usuario");
        }
    }

    @Override
    public Usuario ObtenerPorId(Long id) throws Exception {
        Usuario usuario = repositorio.findById(id).orElse(null);
        if (usuario == null) {
            throw new Exception("No existe el usuario con el id " + id);
        }

        return usuario;
    }

    @Override
    public boolean ExistePorCedula(String cedula) throws Exception {
        Usuario usuario = repositorio.findByCedula(cedula).orElse(null);
        return usuario != null;
    }

    @Override
    public Usuario Editar(Long id, Usuario usuario) throws Exception {
        usuario.validar();
        usuario.setCedula(usuario.getCedula().replaceAll("[./-]", ""));

        Usuario usuarioAModificar = repositorio.findById(id).orElse(null);
        if (usuarioAModificar == null) {
            throw new Exception("No existe el usuario con el id " + id);
        }

        if(usuario.getIdUnidad() != null) {
            Unidad unidad = unidadService.ObtenerPorId(usuario.getIdUnidad()).orElse(null);
            usuarioAModificar.setUnidad(unidad);
        }else{
            usuarioAModificar.setUnidad(null);
        }

        Grado grado = gradoService.ObtenerPorId(usuario.getIdGrado());

        usuarioAModificar.setGrado(grado);
        usuarioAModificar.setNombreCompleto(usuario.getNombreCompleto());
        usuarioAModificar.setRol(usuario.getRol());
        usuarioAModificar.setCedula(usuario.getCedula());
        usuarioAModificar.setActivo(usuario.isActivo());
        usuarioAModificar.setTelefono(usuario.getTelefono());

        if(!usuarioAModificar.isActivo()){
            usuarioAModificar.setCedula(UUID.randomUUID().toString());
        }

        if(usuario.getPassword() != null && !usuario.getPassword().isEmpty()){
            usuarioAModificar.setPassword(securityConfig.passwordEncoder().encode(usuario.getPassword()));
        }

        return repositorio.save(usuarioAModificar);
    }
}
