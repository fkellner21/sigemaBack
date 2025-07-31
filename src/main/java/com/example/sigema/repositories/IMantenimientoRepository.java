package com.example.sigema.repositories;

import com.example.sigema.models.Mantenimiento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface IMantenimientoRepository extends JpaRepository<Mantenimiento, Long> {
    // Buscar por descripción exacta
    Optional<Mantenimiento> findByDescripcion(String descripcion);

    // Buscar todos los mantenimientos de un equipo ordenados por fecha de mantenimiento descendente
    List<Mantenimiento> findByEquipo_IdOrderByFechaMantenimientoDesc(Long idEquipo);

    // Buscar mantenimientos por equipo y fecha de mantenimiento entre dos fechas
    List<Mantenimiento> findByEquipo_IdAndFechaMantenimientoBetween(Long idEquipo, Date desde, Date hasta);

    // Buscar mantenimientos (de cualquier equipo) entre fechas
    List<Mantenimiento> findByFechaMantenimientoBetween(Date desde, Date hasta);
    Optional<Mantenimiento> findTopByEquipo_IdOrderByFechaMantenimientoDesc(Long idEquipo);
}
