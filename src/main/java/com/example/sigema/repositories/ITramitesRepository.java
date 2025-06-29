package com.example.sigema.repositories;

import com.example.sigema.models.Tramite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ITramitesRepository  extends JpaRepository<Tramite, Long> {
    public List<Tramite> findByIdUnidadOrigen(Long idUnidad);
    public List<Tramite> findByIdUnidadDestino(Long idUnidad);
}