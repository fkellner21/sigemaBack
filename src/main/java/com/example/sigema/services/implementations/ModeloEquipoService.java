package com.example.sigema.services.implementations;

import com.example.sigema.models.Marca;
import com.example.sigema.models.ModeloEquipo;
import com.example.sigema.repositories.IModeloEquipoRepository;
import com.example.sigema.services.IMarcaService;
import com.example.sigema.services.IModeloEquipoService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service // mejor que @Repository para servicios
@Transactional
public class ModeloEquipoService implements IModeloEquipoService {
    private final IModeloEquipoRepository modeloEquipoRepository;
    private final IMarcaService marcaService;

    public ModeloEquipoService(IModeloEquipoRepository modeloEquipoRepository, IMarcaService marcaService) {
        this.modeloEquipoRepository = modeloEquipoRepository;
        this.marcaService = marcaService;
    }


    @Override
    public ModeloEquipo Crear(ModeloEquipo modeloEquipo) throws Exception {
        Marca marca = marcaService.ObtenerPorId(modeloEquipo.getIdMarca()).orElse(null);

        if (marca == null) {
            throw new Exception("Marca con ID " + modeloEquipo.getIdMarca() + " no encontrado");
        }

        modeloEquipo.setMarca(marca);

        return modeloEquipoRepository.save(modeloEquipo);
    }

    @Override
    public ModeloEquipo Editar(Long id, ModeloEquipo modeloEquipo) throws Exception {
        ModeloEquipo modeloExistenteOpt = modeloEquipoRepository.findById(id).orElse(null);
        Marca marca = marcaService.ObtenerPorId(modeloEquipo.getIdMarca()).orElse(null);

        if (modeloExistenteOpt == null) {
            throw new Exception("Modelo con ID " + id + " no encontrado");
        }

        if (marca == null) {
            throw new Exception("Marca con ID " + modeloEquipo.getIdMarca() + " no encontrado");
        }

        modeloExistenteOpt.setAnio(modeloEquipo.getAnio());
        modeloExistenteOpt.setModelo(modeloEquipo.getModelo());
        modeloExistenteOpt.setCapacidad(modeloEquipo.getCapacidad());
        modeloExistenteOpt.setMarca(marca);

        return modeloEquipoRepository.save(modeloExistenteOpt);
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