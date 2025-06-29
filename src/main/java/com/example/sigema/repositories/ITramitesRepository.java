package com.example.sigema.repositories;

import com.example.sigema.models.Tramite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import java.util.Optional;


import java.util.List;

public interface ITramitesRepository  extends JpaRepository<Tramite, Long> {

    @EntityGraph(attributePaths = {"actuaciones"})
    Optional<Tramite> findWithActuacionesById(Long id);

    public List<Tramite> findByIdUnidadOrigen(Long idUnidad);
    public List<Tramite> findByIdUnidadDestino(Long idUnidad);
}