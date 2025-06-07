package com.example.sigema.services;

import com.example.sigema.models.DocumentoModeloEquipo;
import com.example.sigema.models.ModeloEquipo;

import java.util.List;
import java.util.Optional;

public interface IDocumentoModeloEquipoService {
    DocumentoModeloEquipo save(DocumentoModeloEquipo doc);
    Optional<DocumentoModeloEquipo> findById(Long id);
    List<DocumentoModeloEquipo> findByModeloEquipo(ModeloEquipo modeloEquipo);
    void delete(DocumentoModeloEquipo doc);

}
