package com.example.sigema.services.implementations;

import com.example.sigema.models.Equipo;
import com.example.sigema.models.ModeloEquipo;
import com.example.sigema.models.Unidad;
import com.example.sigema.repositories.IEquipoRepository;
import com.example.sigema.services.IEquipoService;
import com.example.sigema.services.IModeloEquipoService;
import com.example.sigema.services.IUnidadService;
import com.example.sigema.utilidades.SigemaException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service // mejor que @Repository para servicios
@Transactional
public class EquipoService implements IEquipoService {

    private final IEquipoRepository equipoRepository;
    private final IModeloEquipoService modeloEquipoService;
    private final IUnidadService unidadService;

    @Autowired
    public EquipoService(IEquipoRepository equipoRepository, IModeloEquipoService modeloEquipoService, IUnidadService unidadService) {
        this.equipoRepository = equipoRepository;
        this.modeloEquipoService = modeloEquipoService;
        this.unidadService = unidadService;
    }

    @Override
    public List<Equipo> obtenerTodos() throws Exception {
        return equipoRepository.findAll();
    }

    @Override
    public Equipo Crear(Equipo equipo) throws Exception {
        equipo.validar();

        ModeloEquipo modeloEquipo = modeloEquipoService.ObtenerPorId(equipo.getIdModeloEquipo()).orElse(null);

        if(modeloEquipo == null){
            throw new SigemaException("El modelo de equipo ingresado no existe");
        }

        Unidad unidad = unidadService.ObtenerPorId(equipo.getIdUnidad()).orElse(null);

        if(unidad == null){
            throw new SigemaException("La unidad ingresada no existe");
        }

        equipo.setModeloEquipo(modeloEquipo);
        equipo.setUnidad(unidad);

        return equipoRepository.save(equipo);
    }

    @Override
    public void Eliminar(Long id) throws Exception {
        equipoRepository.deleteById(id);
    }

    @Override
    public Equipo ObtenerPorId(Long id) throws Exception {
        return equipoRepository.findById(id).orElse(null);
    }

    @Override
    public Equipo Editar(Long id, Equipo equipo) throws Exception {
        equipo.validar();

        ModeloEquipo modeloEquipo = modeloEquipoService.ObtenerPorId(equipo.getIdModeloEquipo()).orElse(null);
        Equipo equipoEditar = ObtenerPorId(id);

        if(modeloEquipo == null){
            throw new SigemaException("El modelo de equipo ingresado no existe");
        }

        if (equipoEditar == null) {
            throw new SigemaException("El equipo no existe");
        }

        equipoEditar.setEstado(equipo.getEstado());
        equipoEditar.setCantidadUnidadMedida(equipo.getCantidadUnidadMedida());
        equipoEditar.setMatricula(equipo.getMatricula());
        equipoEditar.setIdUltimaPosicion(equipo.getIdUltimaPosicion());

        return equipoRepository.save(equipoEditar);
    }
}

