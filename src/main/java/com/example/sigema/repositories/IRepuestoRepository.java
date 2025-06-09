package com.example.sigema.repositories;

import com.example.sigema.models.Equipo;
import com.example.sigema.models.Repuesto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IRepuestoRepository extends JpaRepository<Repuesto, Long> {
    Optional<Object> findByNombre(String nombre);
}
