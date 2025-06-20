package com.example.sigema.services.implementations;

import com.example.sigema.models.Grado;
import com.example.sigema.repositories.IRepositoryGrado;
import com.example.sigema.services.IGradoService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GradoService implements IGradoService {

    private final IRepositoryGrado repositorio;

    public GradoService(IRepositoryGrado repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    public List<Grado> obtenerTodos() throws Exception {
        return repositorio.findAll();
    }

    @Override
    public Grado Crear(Grado grado) throws Exception {
        return repositorio.save(grado);
    }

    @Override
    public void Eliminar(Long id) throws Exception {
        repositorio.deleteById(id);
    }

    @Override
    public Grado ObtenerPorId(Long id) throws Exception {
        return repositorio.findById(id)
                .orElseThrow(() -> new Exception("No existe el grado con el id " + id));
    }

    @Override
    public Grado Editar(Long id, Grado grado) throws Exception {
        Grado gradoExistente = repositorio.findById(id)
                .orElseThrow(() -> new Exception("No existe el grado con el id " + id));

        gradoExistente.setNombre(grado.getNombre());

        return repositorio.save(gradoExistente);
    }
}
