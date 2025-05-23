package com.example.sigema.services.implementations;

import com.example.sigema.models.TipoEquipo;
import com.example.sigema.repositories.ITiposEquiposRepository;
import com.example.sigema.services.ITiposEquiposService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TiposEquiposService implements ITiposEquiposService {

    private final ITiposEquiposRepository tiposEquiposRepository;

    public TiposEquiposService(ITiposEquiposRepository tiposEquiposRepository){
        this.tiposEquiposRepository = tiposEquiposRepository;
    }

    @Override
    public TipoEquipo Crear(TipoEquipo tipoEquipo) throws Exception {
        return tiposEquiposRepository.save(tipoEquipo);
    }

    @Override
    public TipoEquipo Editar(Long id, TipoEquipo tipoEquipo) throws Exception {
        TipoEquipo tipoEquipoBuscado = ObtenerPorId(id).orElse(null);

        if(tipoEquipoBuscado == null){
            throw new Exception("El tipo de equipo no existe");
        }

        tipoEquipoBuscado.setNombre(tipoEquipo.getNombre());
        tipoEquipoBuscado.setCodigo(tipoEquipo.getCodigo());
        tipoEquipoBuscado.setActivo(tipoEquipo.isActivo());

        return tiposEquiposRepository.save(tipoEquipoBuscado);
    }

    @Override
    public Optional<TipoEquipo> ObtenerPorId(Long id) {
        return tiposEquiposRepository.findById(id);
    }

    @Override
    public List<TipoEquipo> ObtenerTodos(boolean soloActivos) {
        List<TipoEquipo> tiposEquipos = tiposEquiposRepository.findAll();

        if(soloActivos) {
            tiposEquipos = tiposEquipos.stream().filter(TipoEquipo::isActivo).toList();
        }

        return tiposEquipos;
    }
}
