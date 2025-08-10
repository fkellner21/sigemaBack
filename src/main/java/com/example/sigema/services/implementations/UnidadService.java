package com.example.sigema.services.implementations;

import com.example.sigema.models.Unidad;
import com.example.sigema.models.UnidadEmail;
import com.example.sigema.repositories.IUnidadRepository;
import com.example.sigema.services.ILogService;
import com.example.sigema.services.IUnidadService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.sigema.utilidades.SigemaException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
public class UnidadService implements IUnidadService {

    private final IUnidadRepository unidadRepository;
    private final ILogService logService;

    @Autowired
    public UnidadService(IUnidadRepository unidadRepository, ILogService logService) {
        this.unidadRepository = unidadRepository;
        this.logService = logService;
    }

    @Override
    public List<Unidad> obtenerTodos() throws Exception {
        return unidadRepository.findAll();
    }

    @Override
    public Unidad obtenerGranUnidad() throws SigemaException{
        List<Unidad> unidades = unidadRepository.findByEsGranUnidad(true);
        if (unidades.isEmpty()) throw new SigemaException("No existe en el sistema una Gran Unidad");
        return unidades.get(0);
    }

    @Override
    public Unidad Crear(Unidad unidad) throws Exception {
        unidad.validar();


        if (unidad.getEmails() == null || unidad.getEmails().isEmpty()) {
            throw new SigemaException("La unidad debe tener al menos un email");
        }

        for (UnidadEmail email : unidad.getEmails()) {
            if (email.getEmail() == null || email.getEmail().isBlank()) {
                throw new SigemaException("Todos los emails deben tener un valor válido");
            }
            email.setUnidad(unidad); // Asegurar la relación bidireccional
        }

        List<Unidad> unidades = unidadRepository.findByEsGranUnidad(true);
        if (unidades != null && !unidades.isEmpty() && unidad.isEsGranUnidad()) {
            throw new SigemaException("Ya existe una unidad del tipo gran unidad, solamente puede haber una");
        }

        Unidad creado =  unidadRepository.save(unidad);

        logService.guardarLog("Se ha creado la unidad " + creado.getNombre(), true);

        return creado;
    }

    @Override
    public void Eliminar(Long id) throws Exception {
        Unidad u = ObtenerPorId(id).orElse(null);
        assert u != null;
        String nombre = u.getNombre();

        unidadRepository.deleteById(id);
        logService.guardarLog("Se ha eliminado la unidad " + nombre, true);
    }

    @Override
    public Optional<Unidad> ObtenerPorId(Long id) throws Exception {
        return unidadRepository.findById(id);
    }

    @Override
    public Unidad Editar(Long id, Unidad unidad) throws Exception {
        unidad.validar();
        Unidad unidadEditar = ObtenerPorId(id).orElseThrow(() -> new SigemaException("Unidad no encontrada"));

        List<Unidad> unidades = unidadRepository.findByEsGranUnidad(true);

        if(unidades != null && !unidades.isEmpty() && unidad.isEsGranUnidad() && !Objects.equals(id, unidades.getFirst().getId())){
            throw new SigemaException("Ya existe una unidad del tipo gran unidad, solamente puede haber una");
        }

        unidadEditar.setNombre(unidad.getNombre());
        unidadEditar.setLatitud(unidad.getLatitud());
        unidadEditar.setLongitud(unidad.getLongitud());
        unidadEditar.setEsGranUnidad(unidad.isEsGranUnidad());

        Unidad editado = unidadRepository.save(unidadEditar);
        logService.guardarLog("Se ha editado la unidad " + editado.getNombre(), true);

        return editado;
    }
}