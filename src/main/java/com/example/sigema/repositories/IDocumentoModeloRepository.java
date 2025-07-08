package com.example.sigema.repositories;

import com.example.sigema.models.DocumentoModeloEquipo;
import com.example.sigema.models.Equipo;
import com.example.sigema.models.ModeloEquipo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IDocumentoModeloRepository extends JpaRepository<DocumentoModeloEquipo, Long> {
    List<DocumentoModeloEquipo> findByModeloEquipo(ModeloEquipo modeloEquipo);
}
