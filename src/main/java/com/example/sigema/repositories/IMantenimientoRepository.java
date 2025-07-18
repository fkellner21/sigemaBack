package com.example.sigema.repositories;

import com.example.sigema.models.Mantenimiento;
import com.example.sigema.models.Marca;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IMantenimientoRepository extends JpaRepository<Mantenimiento, Long> {
    Optional<Mantenimiento> findByDescripcion(String descripcion);
}
