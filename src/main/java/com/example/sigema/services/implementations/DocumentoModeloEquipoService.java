package com.example.sigema.services.implementations;

import com.example.sigema.models.DocumentoModeloEquipo;
import com.example.sigema.models.ModeloEquipo;
import com.example.sigema.repositories.IDocumentoModeloRepository;
import com.example.sigema.services.IDocumentoModeloEquipoService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DocumentoModeloEquipoService implements IDocumentoModeloEquipoService {

    private final IDocumentoModeloRepository documentoModeloRepository;

    @Autowired
    public DocumentoModeloEquipoService(IDocumentoModeloRepository documentoModeloRepository) {
        this.documentoModeloRepository = documentoModeloRepository;
    }

    @Override
    public DocumentoModeloEquipo save(DocumentoModeloEquipo doc) {
        return documentoModeloRepository.save(doc);
    }

    @Override
    public Optional<DocumentoModeloEquipo> findById(Long id) {
        return documentoModeloRepository.findById(id);
    }

    @Override
    public List<DocumentoModeloEquipo> findByModeloEquipo(ModeloEquipo modeloEquipo) {
        return documentoModeloRepository.findByModeloEquipo(modeloEquipo);
    }

    @Override
    public void delete(DocumentoModeloEquipo doc) {
        documentoModeloRepository.delete(doc);
    }
}
