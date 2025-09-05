package com.example.sigema.repositories;

import com.example.sigema.models.Mantenimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface IMantenimientoRepository extends JpaRepository<Mantenimiento, Long> {
    // Buscar por descripción exacta
    Optional<Mantenimiento> findByDescripcion(String descripcion);

    // Buscar todos los mantenimientos de un equipo ordenados por fecha de mantenimiento descendente
    List<Mantenimiento> findByEquipo_IdOrderByFechaMantenimientoDesc(Long idEquipo);

    List<Mantenimiento> findByUnidad_IdAndFechaMantenimientoBetween(Long idUnidad, Date desde, Date hasta);

    // Buscar mantenimientos (de cualquier equipo) entre fechas
    List<Mantenimiento> findByFechaMantenimientoBetween(Date desde, Date hasta);
    Optional<Mantenimiento> findTopByEquipo_IdOrderByFechaMantenimientoDesc(Long idEquipo);

    @Modifying
    @Query("DELETE FROM Mantenimiento m WHERE m.id = :id")
    void borrarPorId(@Param("id") Long id);
}
