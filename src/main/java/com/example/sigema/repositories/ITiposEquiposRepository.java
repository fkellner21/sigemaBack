package com.example.sigema.repositories;

import com.example.sigema.models.TipoEquipo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ITiposEquiposRepository extends JpaRepository<TipoEquipo, Long> {
}
