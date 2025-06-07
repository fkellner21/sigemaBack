package com.example.sigema.repositories;

import com.example.sigema.models.Equipo;
import com.example.sigema.models.Unidad;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IUnidadRepository extends JpaRepository<Unidad, Long> {
}