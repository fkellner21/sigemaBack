package com.example.sigema.services.implementations;

import com.example.sigema.models.Marca;
import com.example.sigema.models.Repuesto;
import com.example.sigema.repositories.IMarcaRepository;
import com.example.sigema.repositories.IRepuestoRepository;
import com.example.sigema.services.IMarcaService;
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

    public RepuestoService(IRepuestoRepository rRepository) {
        this.repuestoRepository = rRepository;
    }

    @Override
    public Repuesto Crear(Repuesto r) throws Exception {
        r.validar();

        Repuesto existente = (Repuesto) repuestoRepository.findByNombre(r.getNombre()).orElse(null);

        if(existente != null){
            throw new SigemaException("Ya existe un repuesto con ese nombre");
        }

        return repuestoRepository.save(r);
    }

    @Override
    public Repuesto Editar(Long id, Repuesto r) throws Exception {
        r.validar();

        Repuesto existente = (Repuesto) repuestoRepository.findByNombre(r.getNombre()).orElse(null);



        if(existente != null && !existente.getId().equals(id)){
            throw new SigemaException("Ya existe un repuesto con ese nombre");
        }

        Repuesto rpuestoExt = repuestoRepository.findById(id).orElse(null);

        if (rpuestoExt == null) {
            throw new Exception("Respuesto con ID " + id + " no encontrado");
        }

        rpuestoExt.setNombre(r.getNombre());

        return repuestoRepository.save(rpuestoExt);
    }

    @Override
    public Optional<Repuesto> ObtenerPorId(Long id) throws Exception {
        return repuestoRepository.findById(id);
    }

    @Override
    public List<Repuesto> ObtenerTodos() {
        return repuestoRepository.findAll();
    }
}