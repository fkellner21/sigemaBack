package com.example.sigema.services.implementations;
import com.example.sigema.models.Equipo;
import com.example.sigema.models.Mantenimiento;
import com.example.sigema.models.MantenimientoDTO;
import com.example.sigema.models.Tramite;
import com.example.sigema.repositories.IMantenimientoRepository;
import com.example.sigema.services.IEquipoService;
import com.example.sigema.services.IMantenimientoService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MantenimientoService implements IMantenimientoService {

    private final IMantenimientoRepository repo;
    private final IEquipoService equipoService;

    @Autowired
    public MantenimientoService(IMantenimientoRepository repo, IEquipoService equipoService){
        this.repo = repo;
        this.equipoService = equipoService;
    }

    @Override
    public List<Mantenimiento> obtenerTodos() throws Exception {
            return repo.findAll();
    }

    @Override
    public Optional<Mantenimiento> obtenerPorId(Long id) throws Exception {
        return repo.findById(id);
    }

    @Override
    public Mantenimiento crear(MantenimientoDTO mantenimiento) throws Exception {
        Equipo equipo = equipoService.ObtenerPorId(mantenimiento.getIdEquipo());
        Mantenimiento nuevo = new Mantenimiento();

        if(equipo == null){
            throw new Exception("El equipo no existe");
        }

        LocalDate localDateMantenimiento = LocalDate.parse(mantenimiento.getFechaMantenimiento());
        Date fechaMantenimiento = Date.from(localDateMantenimiento.atStartOfDay(ZoneId.systemDefault()).toInstant());

        LocalDate localDateRegistro = LocalDate.now();
        Date fechaRegistro = Date.from(localDateRegistro.atStartOfDay(ZoneId.systemDefault()).toInstant());

        nuevo.setFechaRegistro(fechaRegistro);
        nuevo.setFechaMantenimiento(fechaMantenimiento);
        nuevo.setEquipo(equipo);
        nuevo.setEsService(mantenimiento.isEsService());
        nuevo.setDescripcion(mantenimiento.getDescripcion());
        nuevo.setUnidadMedida(mantenimiento.getUnidadMedida());
        nuevo.setCantidadUnidadMedida(mantenimiento.getCantidadUnidadMedida());

        return repo.save(nuevo);
    }

    @Override
    public Mantenimiento editar(Long id, MantenimientoDTO mantenimientoActualizado) throws Exception {
        Mantenimiento existente = repo.findById(id).orElse(null);
        if (existente != null) {
            Equipo equipo = equipoService.ObtenerPorId(mantenimientoActualizado.getIdEquipo());

            if(equipo == null){
                throw new Exception("El equipo no existe");
            }

            LocalDate localDateMantenimiento = LocalDate.parse(mantenimientoActualizado.getFechaMantenimiento());
            Date fechaMantenimiento = Date.from(localDateMantenimiento.atStartOfDay(ZoneId.systemDefault()).toInstant());

            existente.setEquipo(equipo);
            existente.setDescripcion(mantenimientoActualizado.getDescripcion());
            existente.setFechaMantenimiento(fechaMantenimiento);
            existente.getRepuestosMantenimiento().clear();
            existente.getRepuestosMantenimiento().addAll(mantenimientoActualizado.getRepuestosMantenimiento());
            existente.setUnidadMedida(mantenimientoActualizado.getUnidadMedida());
            existente.setCantidadUnidadMedida(mantenimientoActualizado.getCantidadUnidadMedida());
            existente.setEsService(mantenimientoActualizado.isEsService());

            return repo.save(existente);
        }

        return null;
    }

    @Override
    public void eliminar(Long id) {
        repo.deleteById(id);
    }

    @Override
    public List<Mantenimiento> obtenerPorEquipo(Long idEquipo) throws Exception {
        return repo.findByEquipo_IdOrderByFechaMantenimientoDesc(idEquipo);
    }
    @Override
    public List<Mantenimiento> ObtenerTodosPorFechas(Long idEquipo, Date desde, Date hasta) throws Exception {
        ZoneId zone = ZoneId.of("America/Montevideo");

        // Normalizar fechas a inicio y fin del día
        LocalDate localDesde = desde.toInstant().atZone(zone).toLocalDate();
        LocalDate localHasta = hasta.toInstant().atZone(zone).toLocalDate();

        Date fechaDesde = Date.from(localDesde.atStartOfDay(zone).toInstant());
        Date fechaHasta = Date.from(localHasta.atTime(LocalTime.MAX).atZone(zone).toInstant());

        List<Mantenimiento> mantenimientos;

        if (idEquipo == null || idEquipo == 0) {
            // Buscar todos los mantenimientos entre las fechas
            mantenimientos = repo.findByFechaMantenimientoBetween(fechaDesde, fechaHasta);
        } else {
            // Buscar solo los mantenimientos de ese equipo entre las fechas
            mantenimientos = repo.findByEquipo_IdAndFechaMantenimientoBetween(idEquipo, fechaDesde, fechaHasta);
        }

        return mantenimientos;
    }


}
