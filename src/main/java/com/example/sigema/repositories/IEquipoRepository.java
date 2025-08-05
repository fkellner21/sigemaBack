package com.example.sigema.repositories;

import com.example.sigema.models.Equipo;
import com.example.sigema.models.enums.EstadoEquipo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IEquipoRepository extends JpaRepository<Equipo, Long> {

    List<Equipo> findByModeloEquipoId(Long idModelo);
    List<Equipo> findByUnidad_IdAndActivoTrue(Long idUnidad);
    List<Equipo> findByModeloEquipoIdAndUnidad_Id(Long idModelo, Long idUnidad);
    Equipo findByMatricula(String matricula);
    List<Equipo> findByActivoTrue();
}