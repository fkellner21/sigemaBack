package com.example.sigema.services.implementations;

import com.example.sigema.models.ModeloEquipo;
import com.example.sigema.models.Repuesto;
import com.example.sigema.models.enums.TipoRepuesto;
import com.example.sigema.repositories.IRepuestoRepository;
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

    public RepuestoService(IRepuestoRepository rRepository, IModeloEquipoService modeloEquipoService) {
        this.repuestoRepository = rRepository;
        this.modeloEquipoService = modeloEquipoService;
    }

    @Override
    public Repuesto Crear(Repuesto r) throws Exception {
        r.validar();

        ModeloEquipo modelo = modeloEquipoService.ObtenerPorId(r.getIdModelo()).orElse(null);

        if(modelo == null){
            throw new SigemaException("No existe el modelo");
        }

        r.setModeloEquipo(modelo);

        return repuestoRepository.save(r);
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

        return repuestoRepository.save(rpuestoExt);
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