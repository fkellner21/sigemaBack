package com.example.sigema.services.implementations;

import com.example.sigema.models.Equipo;
import com.example.sigema.repositories.IEquipoRepository;
import com.example.sigema.services.IEquipoService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service // mejor que @Repository para servicios
@Transactional
public class EquipoService implements IEquipoService {

    private final IEquipoRepository equipoRepository;

    @Autowired
    public EquipoService(IEquipoRepository equipoRepository) {
        this.equipoRepository = equipoRepository;
    }

    @Override
    public List<Equipo> obtenerTodos() throws Exception {
        return equipoRepository.findAll();
    }

    @Override
    public Equipo Crear(Equipo equipo) throws Exception {
        return equipoRepository.save(equipo);
    }

    @Override
    public void Eliminar(Long id) throws Exception {
        equipoRepository.deleteById(id);
    }

    @Override
    public Optional<Equipo> ObtenerPorId(Long id) throws Exception {
        return equipoRepository.findById(id);
    }

    @Override
    public Equipo Editar(Long id, Equipo equipo) throws Exception {
        Equipo equipoEditar = ObtenerPorId(id).orElse(null);

        if(equipoEditar == null){
            throw new Exception("El equipo no existe");
        }

        equipoEditar.setEstado(equipo.getEstado());
        equipoEditar.setCantidadUnidadMedida(equipo.getCantidadUnidadMedida());
        equipoEditar.setMatricula(equipo.getMatricula());
        equipoEditar.setUnidadMedida(equipo.getUnidadMedida());
        equipoEditar.setIdUltimaPosicion(equipo.getIdUltimaPosicion();

        return equipoRepository.save(equipoEditar);
    }
}

