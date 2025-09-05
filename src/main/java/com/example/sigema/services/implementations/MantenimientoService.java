package com.example.sigema.services.implementations;
import com.example.sigema.models.*;
import com.example.sigema.repositories.IMantenimientoRepository;
import com.example.sigema.repositories.IRepuestoMantenimientoRepository;
import com.example.sigema.services.*;
import com.example.sigema.utilidades.SigemaException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.transaction.annotation.Propagation;

@Service
@Transactional
public class MantenimientoService implements IMantenimientoService {

    private final IMantenimientoRepository repo;
    private final IEquipoService equipoService;
    private final IRepuestoService repuestoService;
    private final ILogService logService;
    private final IRepuestoMantenimientoRepository repuestoMantenimientoRepository;

    @Autowired
    public MantenimientoService(IMantenimientoRepository repo, IEquipoService equipoService, IRepuestoService repuestoService, ILogService logService,
                                IRepuestoMantenimientoRepository repuestoMantenimientoRepository){
        this.repo = repo;
        this.equipoService = equipoService;
        this.repuestoService = repuestoService;
        this.logService = logService;
        this.repuestoMantenimientoRepository = repuestoMantenimientoRepository;
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

        for(RepuestoMantenimiento r : mantenimiento.getRepuestosMantenimiento()){
            Repuesto repuesto = repuestoService.ObtenerPorId(r.getIdRepuesto()).orElse(null);

            if(repuesto == null){
                throw new Exception("Repuesto no encontrado");
            }

            r.setRepuesto(repuesto);
        }

        nuevo.setRepuestosMantenimiento(mantenimiento.getRepuestosMantenimiento());
        nuevo.validar();

        Mantenimiento creado = repo.save(nuevo);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        String fechaHora = String.format(String.valueOf(creado.getFechaMantenimiento()), formatter);
        logService.guardarLog("Se ha creado un mantenimiento (Fecha: " + fechaHora + ") para el equipo (Matrícula: " + equipo.getMatricula() + ", Modelo: " + equipo.getModeloEquipo().getModelo() + ")", true);

        return creado;
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
            existente.setUnidadMedida(mantenimientoActualizado.getUnidadMedida());
            existente.setCantidadUnidadMedida(mantenimientoActualizado.getCantidadUnidadMedida());
            existente.setEsService(mantenimientoActualizado.isEsService());

            List<RepuestoMantenimiento> actuales = existente.getRepuestosMantenimiento();
            actuales.clear();

            for (RepuestoMantenimiento r : mantenimientoActualizado.getRepuestosMantenimiento()) {
                Long idRepuesto = r.getRepuesto() != null ? r.getRepuesto().getId() : r.getIdRepuesto();
                Repuesto repuesto = repuestoService.ObtenerPorId(idRepuesto).orElse(null);

                if(repuesto == null){
                    throw new Exception("Repuesto no encontrado");
                }

                r.setRepuesto(repuesto);
                actuales.add(r);
            }

            existente.setRepuestosMantenimiento(actuales);
            existente.validar();
            Mantenimiento editado = repo.save(existente);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            String fechaHora = String.format(mantenimientoActualizado.getFechaMantenimiento(), formatter);
            logService.guardarLog("Se ha editado un mantenimiento (Fecha: " + fechaHora + ") para el equipo (Matrícula: " + equipo.getMatricula() + ", Modelo: " + equipo.getModeloEquipo().getModelo() + ")", true);

            return editado;
        }

        return null;
    }

    @Override
    @Transactional
    public void eliminar(Long id) throws Exception {
        Mantenimiento el = obtenerPorId(id)
                .orElseThrow(() -> new SigemaException("Mantenimiento no encontrado"));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        String fechaHora = el.getFechaMantenimiento()
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .format(formatter);

        Equipo equipo = equipoService.ObtenerPorId(el.getEquipo().getId());

        repuestoMantenimientoRepository.borrarPorMantenimiento(id);
        repo.borrarPorId(id);

        logService.guardarLog(
                "Se ha eliminado un mantenimiento (Fecha: " + fechaHora +
                        ") para el equipo (Matricula: " + equipo.getMatricula() +
                        ", Modelo: " + equipo.getModeloEquipo().getModelo() + ")",
                true
        );
    }

    @Override
    public List<Mantenimiento> obtenerPorEquipo(Long idEquipo) throws Exception {
        return repo.findByEquipo_IdOrderByFechaMantenimientoDesc(idEquipo);
    }
    @Override
    public List<Mantenimiento> ObtenerTodosPorFechas(Long idUnidad, Date desde, Date hasta) throws Exception {
        ZoneId zone = ZoneId.of("America/Montevideo");

        // Normalizar fechas a inicio y fin del día
        LocalDate localDesde = desde.toInstant().atZone(zone).toLocalDate();
        LocalDate localHasta = hasta.toInstant().atZone(zone).toLocalDate();

        Date fechaDesde = Date.from(localDesde.atStartOfDay(zone).toInstant());
        Date fechaHasta = Date.from(localHasta.atTime(LocalTime.MAX).atZone(zone).toInstant());

        List<Mantenimiento> mantenimientos;

        if (idUnidad == null || idUnidad == 0) {
            mantenimientos = repo.findByFechaMantenimientoBetween(fechaDesde, fechaHasta);
        } else {
            mantenimientos = repo.findByEquipo_Unidad_IdAndFechaMantenimientoBetween(idUnidad, fechaDesde, fechaHasta);
        }

        return mantenimientos;
    }

    @Override
    public Mantenimiento ObtenerUltimoMantenimientoPorIdEquipo(Long idEquipo) {
        return repo.findTopByEquipo_IdOrderByFechaMantenimientoDesc(idEquipo).orElse(null);
    }
}
