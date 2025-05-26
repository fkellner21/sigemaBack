package com.example.sigema.repositories;

import com.example.sigema.models.Equipo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IEquipoRepository extends JpaRepository<Equipo, Long> {
}
