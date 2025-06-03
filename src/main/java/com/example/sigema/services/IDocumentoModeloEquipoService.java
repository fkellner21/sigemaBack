package com.example.sigema.services;

import com.example.sigema.models.DocumentoModeloEquipo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IDocumentoModeloEquipoService extends JpaRepository<DocumentoModeloEquipo, Long> {
    List<DocumentoModeloEquipo> findByModeloEquipoId(Long modeloEquipoId);
}