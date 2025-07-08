package com.example.sigema.repositories;

import com.example.sigema.models.Equipo;
import com.example.sigema.models.Tramite;
import com.example.sigema.models.Unidad;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;
import java.util.Optional;

public interface IUnidadRepository extends JpaRepository<Unidad, Long> {
    ArrayList<Unidad> findByEsGranUnidad(boolean esGranUnidad);
}