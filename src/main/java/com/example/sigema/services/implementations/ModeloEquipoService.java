package com.example.sigema.services.implementations;

import com.example.sigema.models.ModeloEquipo;
import com.example.sigema.repositories.IModeloEquipoRepository;
import com.example.sigema.services.IModeloEquipoService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service // mejor que @Repository para servicios
@Transactional
public class ModeloEquipoService implements IModeloEquipoService {
    private final IModeloEquipoRepository modeloEquipoRepository;


    public ModeloEquipoService(IModeloEquipoRepository modeloEquipoRepository) {
        this.modeloEquipoRepository = modeloEquipoRepository;
    }


    @Override
    public ModeloEquipo Crear(ModeloEquipo modeloEquipo) throws Exception {
        return modeloEquipoRepository.save(modeloEquipo);
    }


    @Override
    public ModeloEquipo Editar(Long id, ModeloEquipo modeloEquipo) throws Exception {
        Optional<ModeloEquipo> modeloExistenteOpt = modeloEquipoRepository.findById(id);

        if (modeloExistenteOpt.isEmpty()) {
            throw new Exception("Modelo con ID " + id + " no encontrado");
        }

        ModeloEquipo existente = modeloExistenteOpt.get();

        existente.setAnio(modeloEquipo.getAnio());
        existente.setModelo(modeloEquipo.getModelo());
        existente.setCapacidad(modeloEquipo.getCapacidad());
        existente.setIdMarca(modeloEquipo.getIdMarca());
        existente.setIdEquipos(modeloEquipo.getIdEquipos());
        existente.setIdRepuestos(modeloEquipo.getIdRepuestos());

        return modeloEquipoRepository.save(existente);
    }


    @Override
    public Optional<ModeloEquipo> ObtenerPorId(Long id) throws Exception {
        return modeloEquipoRepository.findById(id);
    }

    @Override
    public List<ModeloEquipo> ObtenerTodos() {
        return modeloEquipoRepository.findAll();
    }
}
