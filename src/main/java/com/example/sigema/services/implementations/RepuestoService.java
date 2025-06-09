package com.example.sigema.services.implementations;

import com.example.sigema.models.Marca;
import com.example.sigema.models.Repuesto;
import com.example.sigema.models.enums.TipoRepuesto;
import com.example.sigema.repositories.IMarcaRepository;
import com.example.sigema.repositories.IRepuestoRepository;
import com.example.sigema.services.IMarcaService;
import com.example.sigema.services.IRepuestoService;
import com.example.sigema.utilidades.SigemaException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RepuestoService implements IRepuestoService
{
    private final IRepuestoRepository repuestoRepository;

    public RepuestoService(IRepuestoRepository rRepository) {
        this.repuestoRepository = rRepository;
    }

    @Override
    public Repuesto Crear(Repuesto r) throws Exception {
        r.validar();

        Repuesto existenteCodigoSice = repuestoRepository.findByCodigoSICE(r.getCodigoSICE()).orElse(null);

        if(existenteCodigoSice != null){
            throw new SigemaException("Ya existe un repuesto con ese código SICE");
        }

        Repuesto existente = repuestoRepository.findByNombre(r.getNombre()).orElse(null);

        if(existente != null){
            throw new SigemaException("Ya existe un repuesto con ese nombre");
        }

        return repuestoRepository.save(r);
    }

    @Override
    public Repuesto Editar(Long id, Repuesto r) throws Exception {
        r.validar();

        Repuesto existenteCodigoSice = repuestoRepository.findByCodigoSICE(r.getCodigoSICE()).orElse(null);

        if(existenteCodigoSice != null){
            throw new SigemaException("Ya existe un repuesto con ese código SICE");
        }

        Repuesto existente = repuestoRepository.findByNombre(r.getNombre()).orElse(null);

        if(existente != null && !existente.getId().equals(id)){
            throw new SigemaException("Ya existe un repuesto con ese nombre");
        }

        Repuesto rpuestoExt = repuestoRepository.findById(id).orElse(null);

        if (rpuestoExt == null) {
            throw new Exception("Repuesto con ID " + id + " no encontrado");
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