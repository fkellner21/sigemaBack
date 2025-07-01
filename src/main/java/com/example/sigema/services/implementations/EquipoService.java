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

import java.util.Date;
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
    public List<Equipo> obtenerTodos(Long idUnidad) throws Exception {
        if(idUnidad == null || idUnidad == 0){
            return equipoRepository.findAll();
        }else{
            return equipoRepository.findByUnidad_Id(idUnidad);
        }
    }

    @Override
    public Equipo Crear(Equipo equipo) throws Exception {
        equipo.validar();

        Equipo equipoExistente = equipoRepository.findByMatricula(equipo.getMatricula().toUpperCase());
        if(equipoExistente != null){
            throw new SigemaException("Ya existe un equipo con esa matricula");
        }

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
        equipo.setMatricula(equipo.getMatricula().toUpperCase());
        equipo.setLatitud(unidad.getLatitud());
        equipo.setLongitud(unidad.getLongitud());
        equipo.setFechaUltimaPosicion(new Date());

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
        equipoEditar.setMatricula(equipo.getMatricula().toUpperCase());
        equipoEditar.setIdUnidad(equipo.getIdUnidad());
        equipoEditar.setIdModeloEquipo(equipo.getIdModeloEquipo());
        equipoEditar.setObservaciones(equipo.getObservaciones());

        return equipoRepository.save(equipoEditar);
    }

    @Override
    public List<Equipo> obtenerEquiposPorIdModelo(Long idModelo, Long idUnidad) {
        if(idUnidad == null || idUnidad == 0){
            return equipoRepository.findByModeloEquipoId(idModelo);
        }else{
            return equipoRepository.findByModeloEquipoIdAndUnidad_Id(idModelo, idUnidad);
        }
    }
}

