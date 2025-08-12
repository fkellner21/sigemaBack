package com.example.sigema.services.implementations;

import com.example.sigema.models.Marca;
import com.example.sigema.models.ModeloEquipo;
import com.example.sigema.models.TipoEquipo;
import com.example.sigema.repositories.IModeloEquipoRepository;
import com.example.sigema.services.ILogService;
import com.example.sigema.services.IMarcaService;
import com.example.sigema.services.IModeloEquipoService;
import com.example.sigema.services.ITiposEquiposService;
import com.example.sigema.utilidades.SigemaException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ModeloEquipoService implements IModeloEquipoService {
    private final IModeloEquipoRepository modeloEquipoRepository;
    private final IMarcaService marcaService;
    private final ITiposEquiposService tiposEquiposService;
    private final ILogService logService;

    public ModeloEquipoService(IModeloEquipoRepository modeloEquipoRepository, IMarcaService marcaService, ITiposEquiposService tiposEquiposService, ILogService logService) {
        this.modeloEquipoRepository = modeloEquipoRepository;
        this.marcaService = marcaService;
        this.tiposEquiposService = tiposEquiposService;
        this.logService = logService;
    }

    @Override
    public ModeloEquipo Crear(ModeloEquipo modeloEquipo) throws Exception {
        modeloEquipo.validar();

        Marca marca = marcaService.ObtenerPorId(modeloEquipo.getIdMarca()).orElse(null);
        TipoEquipo tipoEquipo = tiposEquiposService.ObtenerPorId(modeloEquipo.getIdTipoEquipo()).orElse(null);

        if (marca == null) {
            throw new SigemaException("Marca con ID " + modeloEquipo.getIdMarca() + " no encontrado");
        }

        if (tipoEquipo == null) {
            throw new SigemaException("Tipo de Equipo con ID " + modeloEquipo.getIdTipoEquipo() + " no encontrado");
        }

        modeloEquipo.setMarca(marca);
        modeloEquipo.setTipoEquipo(tipoEquipo);

        ModeloEquipo creado = modeloEquipoRepository.save(modeloEquipo);
        logService.guardarLog("Se ha creado el modelo equipo " + creado.getModelo(), true);

        return creado;
    }

    @Override
    public ModeloEquipo Editar(Long id, ModeloEquipo modeloEquipo) throws Exception {
        modeloEquipo.validar();

        ModeloEquipo modeloExistenteOpt = modeloEquipoRepository.findById(id).orElse(null);
        Marca marca = marcaService.ObtenerPorId(modeloEquipo.getIdMarca()).orElse(null);
        TipoEquipo tipoEquipo = tiposEquiposService.ObtenerPorId(modeloEquipo.getIdTipoEquipo()).orElse(null);

        if (modeloExistenteOpt == null) {
            throw new SigemaException("Modelo con ID " + id + " no encontrado");
        }

        if (marca == null) {
            throw new SigemaException("Marca con ID " + modeloEquipo.getIdMarca() + " no encontrado");
        }

        if (tipoEquipo == null) {
            throw new SigemaException("Tipo de Equipo con ID " + modeloEquipo.getIdTipoEquipo() + " no encontrado");
        }

        modeloExistenteOpt.setAnio(modeloEquipo.getAnio());
        modeloExistenteOpt.setModelo(modeloEquipo.getModelo());
        modeloExistenteOpt.setCapacidad(modeloEquipo.getCapacidad());
        modeloExistenteOpt.setMarca(marca);
        modeloExistenteOpt.setTipoEquipo(tipoEquipo);
        modeloExistenteOpt.setUnidadMedida(modeloEquipo.getUnidadMedida());
        modeloExistenteOpt.setFrecuenciaTiempo(modeloEquipo.getFrecuenciaTiempo());
        modeloExistenteOpt.setFrecuenciaUnidadMedida(modeloEquipo.getFrecuenciaUnidadMedida());
        modeloExistenteOpt.setServiceModelo(modeloEquipo.getServiceModelo());

        ModeloEquipo editado = modeloEquipoRepository.save(modeloExistenteOpt);
        logService.guardarLog("Se ha editado el modelo equipo " + editado.getModelo(), true);

        return editado;
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