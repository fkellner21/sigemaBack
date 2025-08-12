package com.example.sigema.services.implementations;

import com.example.sigema.models.ModeloEquipo;
import com.example.sigema.models.Repuesto;
import com.example.sigema.models.enums.TipoRepuesto;
import com.example.sigema.repositories.IRepuestoRepository;
import com.example.sigema.services.ILogService;
import com.example.sigema.services.IModeloEquipoService;
import com.example.sigema.services.IRepuestoService;
import com.example.sigema.utilidades.SigemaException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RepuestoService implements IRepuestoService
{
    private final IRepuestoRepository repuestoRepository;
    private final IModeloEquipoService modeloEquipoService;
    private final ILogService logService;

    public RepuestoService(IRepuestoRepository rRepository, IModeloEquipoService modeloEquipoService, ILogService logService) {
        this.repuestoRepository = rRepository;
        this.modeloEquipoService = modeloEquipoService;
        this.logService = logService;
    }

    @Override
    public Repuesto Crear(Repuesto r) throws Exception {
        r.validar();

        ModeloEquipo modelo = modeloEquipoService.ObtenerPorId(r.getIdModelo()).orElse(null);

        if(modelo == null){
            throw new SigemaException("No existe el modelo");
        }

        r.setModeloEquipo(modelo);

        Repuesto creado = repuestoRepository.save(r);

        logService.guardarLog("Se ha creado el repuesto (SICE: " + creado.getCodigoSICE() + ", Nombre: " + creado.getNombre() + ") para el modelo equipo " + modelo.getModelo(), true);

        return creado;
    }

    @Override
    public Repuesto Editar(Long id, Repuesto r) throws Exception {
        r.validar();

        Repuesto existenteCodigoSice = repuestoRepository.findByCodigoSICE(r.getCodigoSICE()).orElse(null);

        if(existenteCodigoSice != null && !existenteCodigoSice.getId().equals(id)){
            throw new SigemaException("Ya existe un repuesto con ese código SICE");
        }

       Repuesto rpuestoExt = repuestoRepository.findById(id).orElse(null);

        if (rpuestoExt == null) {
            throw new SigemaException("Repuesto con ID " + id + " no encontrado");
        }

        rpuestoExt.setNombre(r.getNombre());
        rpuestoExt.setTipo(r.getTipo());
        rpuestoExt.setCantidad(r.getCantidad());
        rpuestoExt.setCodigoSICE(r.getCodigoSICE());
        rpuestoExt.setCaracteristicas(r.getCaracteristicas());
        rpuestoExt.setObservaciones(r.getObservaciones());

        Repuesto editado = repuestoRepository.save(rpuestoExt);

        logService.guardarLog("Se ha editado el repuesto (SICE: " + editado.getCodigoSICE() + ", Nombre: " + editado.getNombre() + ") para el modelo equipo " + rpuestoExt.getModeloEquipo().getModelo(), true);

        return editado;
    }

    @Override
    public Optional<Repuesto> ObtenerPorId(Long id) throws Exception {
        return repuestoRepository.findById(id);
    }

    @Override
    public List<Repuesto> ObtenerTodos(Long idModelo, TipoRepuesto tipoRepuesto) {
        return repuestoRepository.findByIdModeloAndTipo(idModelo, tipoRepuesto);
    }
}