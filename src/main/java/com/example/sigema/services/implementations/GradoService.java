package com.example.sigema.services.implementations;

import com.example.sigema.models.Grado;
import com.example.sigema.repositories.IRepositoryGrado;
import com.example.sigema.services.IGradoService;
import com.example.sigema.services.ILogService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GradoService implements IGradoService {

    private final IRepositoryGrado repositorio;
    private final ILogService logService;

    public GradoService(IRepositoryGrado repositorio, ILogService logService) {
        this.repositorio = repositorio;
        this.logService = logService;
    }

    @Override
    public List<Grado> obtenerTodos() throws Exception {
        return repositorio.findAll();
    }

    @Override
    public Grado Crear(Grado grado) throws Exception {
        Grado gradoGuardado = null;
        gradoGuardado = repositorio.save(grado);

        logService.guardarLog("Se ha creado el grado " + grado.getNombre(), true);

        return gradoGuardado;
    }

    @Override
    public void Eliminar(Long id) throws Exception {
        Grado grado = ObtenerPorId(id);
        String nombreGrado = grado.getNombre();
        repositorio.deleteById(id);

        logService.guardarLog("Se ha eliminado el grado " + nombreGrado, true);
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

        Grado gEditado = repositorio.save(gradoExistente);
        logService.guardarLog("Se ha editado el grado " + gEditado.getNombre(), true);

        return gEditado;
    }
}
