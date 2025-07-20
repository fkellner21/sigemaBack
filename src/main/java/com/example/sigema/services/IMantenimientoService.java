package com.example.sigema.services;



import com.example.sigema.models.Mantenimiento;

import java.util.List;
import java.util.Optional;

public interface IMantenimientoService {
    List<Mantenimiento> obtenerTodos() throws Exception;

    Mantenimiento crear(Mantenimiento r) throws Exception;

    Optional<Mantenimiento> obtenerPorId(Long id) throws Exception;

    Mantenimiento editar(Long id, Mantenimiento r) throws Exception;

    void eliminar(Long id) throws Exception;

    List<Mantenimiento> obtenerPorEquipo(Long idEquipo) throws Exception;
}

