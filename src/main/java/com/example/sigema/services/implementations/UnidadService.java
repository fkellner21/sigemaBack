package com.example.sigema.services.implementations;

import com.example.sigema.models.Unidad;
import com.example.sigema.repositories.IUnidadRepository;
import com.example.sigema.services.IUnidadService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UnidadService implements IUnidadService {

    private final IUnidadRepository unidadRepository;

    @Autowired
    public UnidadService(IUnidadRepository unidadRepository) {
        this.unidadRepository = unidadRepository;
    }

    @Override
    public List<Unidad> obtenerTodos() throws Exception {
        return unidadRepository.findAll();
    }

    @Override
    public Unidad Crear(Unidad unidad) throws Exception {
        unidad.validar();
        return unidadRepository.save(unidad);
    }

    @Override
    public void Eliminar(Long id) throws Exception {
        unidadRepository.deleteById(id);
    }

    @Override
    public Optional<Unidad> ObtenerPorId(Long id) throws Exception {
        return unidadRepository.findById(id);
    }

    @Override
    public Unidad Editar(Long id, Unidad unidad) throws Exception {
        unidad.validar();
        Unidad unidadEditar = ObtenerPorId(id).orElseThrow(() -> new SigemaException("Unidad no encontrada"));
        unidadEditar.setNombre(unidad.getNombre());
        return unidadRepository.save(unidadEditar);
    }
}
