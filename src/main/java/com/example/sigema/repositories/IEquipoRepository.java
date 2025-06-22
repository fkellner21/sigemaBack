package com.example.sigema.repositories;

import com.example.sigema.models.Equipo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IEquipoRepository extends JpaRepository<Equipo, Long> {

    public List<Equipo> findByModeloEquipoId(Long idModelo);
    public List<Equipo> findByUnidad_Id(Long idUnidad);
    public List<Equipo> findByModeloEquipoIdAndUnidad_Id(Long idModelo, Long idUnidad);
}