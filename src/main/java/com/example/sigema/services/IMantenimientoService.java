package com.example.sigema.services;



import com.example.sigema.models.Mantenimiento;
import com.example.sigema.models.MantenimientoDTO;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface IMantenimientoService {
    List<Mantenimiento> obtenerTodos() throws Exception;

    Mantenimiento crear(MantenimientoDTO r) throws Exception;

    Optional<Mantenimiento> obtenerPorId(Long id) throws Exception;

    Mantenimiento editar(Long id, MantenimientoDTO r) throws Exception;

    void eliminar(Long id) throws Exception;

    List<Mantenimiento> obtenerPorEquipo(Long idEquipo) throws Exception;

    List<Mantenimiento> ObtenerTodosPorFechas(Long idUnidad, Date fechaDesde, Date fechaHasta) throws Exception;
    Mantenimiento ObtenerUltimoMantenimientoPorIdEquipo(Long idEquipo);
}

