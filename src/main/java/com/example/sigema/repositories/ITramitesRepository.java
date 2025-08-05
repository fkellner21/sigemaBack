package com.example.sigema.repositories;

import com.example.sigema.models.Tramite;
import com.example.sigema.models.enums.EstadoTramite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.Date;
import java.util.Optional;


import java.util.List;

public interface ITramitesRepository  extends JpaRepository<Tramite, Long> {

    @EntityGraph(attributePaths = {"actuaciones"})
    Optional<Tramite> findWithActuacionesById(Long id);
    List<Tramite> findByUnidadOrigen_Id(Long idUnidad);
    List<Tramite> findByUnidadDestino_Id(Long idUnidad);
    List<Tramite> findByFechaInicioBetween(Date desde, Date hasta);
    List<Tramite> findByUnidadOrigen_IdAndFechaInicioBetween(Long idUnidad, Date desde, Date hasta);
    List<Tramite> findByUnidadDestino_IdAndFechaInicioBetween(Long idUnidad, Date desde, Date hasta);
    List<Tramite> findByEquipo_IdAndEstadoIn(Long idEquipo, List<EstadoTramite> estados);
}