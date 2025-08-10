package com.example.sigema.services.implementations;

import com.example.sigema.models.TipoEquipo;
import com.example.sigema.repositories.ITiposEquiposRepository;
import com.example.sigema.services.ILogService;
import com.example.sigema.services.ITiposEquiposService;
import com.example.sigema.utilidades.SigemaException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TiposEquiposService implements ITiposEquiposService {

    private final ITiposEquiposRepository tiposEquiposRepository;
    private final ILogService logService;

    public TiposEquiposService(ITiposEquiposRepository tiposEquiposRepository, ILogService logService) {
        this.tiposEquiposRepository = tiposEquiposRepository;
        this.logService = logService;
    }

    @Override
    public TipoEquipo Crear(TipoEquipo tipoEquipo) throws Exception {
        tipoEquipo.validar();

        TipoEquipo existente = tiposEquiposRepository.findByCodigo(tipoEquipo.getCodigo()).orElse(null);

        if(existente != null){
            throw new SigemaException("Ya existe un tipo de equipo con el código " + tipoEquipo.getCodigo());
        }

        TipoEquipo creado = tiposEquiposRepository.save(tipoEquipo);
        logService.guardarLog("Se ha creado el tipo equipo " + creado.getNombre(), true);

        return creado;
    }

    @Override
    public TipoEquipo Editar(Long id, TipoEquipo tipoEquipo) throws Exception {
        tipoEquipo.validar();

        TipoEquipo existente = tiposEquiposRepository.findByCodigo(tipoEquipo.getCodigo()).orElse(null);

        if(existente != null && existente.getId() != null && !existente.getId().equals(id)){
            throw new SigemaException("Ya existe un tipo de equipo con el código " + tipoEquipo.getCodigo());
        }

        TipoEquipo tipoEquipoBuscado = ObtenerPorId(id).orElse(null);

        if (tipoEquipoBuscado == null) {
            throw new SigemaException("El tipo de equipo ha editar no existe");
        }

        tipoEquipoBuscado.setNombre(tipoEquipo.getNombre());
        tipoEquipoBuscado.setCodigo(tipoEquipo.getCodigo());
        tipoEquipoBuscado.setActivo(tipoEquipo.isActivo());

        TipoEquipo editado = tiposEquiposRepository.save(tipoEquipoBuscado);
        logService.guardarLog("Se ha editado el tipo equipo " + editado.getNombre(), true);

        return editado;
    }

    @Override
    public Optional<TipoEquipo> ObtenerPorId(Long id) {
        return tiposEquiposRepository.findById(id);
    }

    @Override
    public List<TipoEquipo> ObtenerTodos(boolean soloActivos) {
        List<TipoEquipo> tiposEquipos = tiposEquiposRepository.findAll();

        if (soloActivos) {
            tiposEquipos = tiposEquipos.stream().filter(TipoEquipo::isActivo).toList();
        }

        return tiposEquipos;
    }
}
