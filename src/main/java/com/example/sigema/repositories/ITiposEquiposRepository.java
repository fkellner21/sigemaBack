package com.example.sigema.repositories;

import com.example.sigema.models.TipoEquipo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ITiposEquiposRepository extends JpaRepository<TipoEquipo, Long> {
    Optional<TipoEquipo> findByCodigo(String codigo);
}
