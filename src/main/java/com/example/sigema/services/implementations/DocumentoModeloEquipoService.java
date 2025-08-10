package com.example.sigema.services.implementations;

import com.example.sigema.models.DocumentoModeloEquipo;
import com.example.sigema.models.ModeloEquipo;
import com.example.sigema.repositories.IDocumentoModeloRepository;
import com.example.sigema.services.IDocumentoModeloEquipoService;
import com.example.sigema.services.ILogService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DocumentoModeloEquipoService implements IDocumentoModeloEquipoService {

    private final IDocumentoModeloRepository documentoModeloRepository;
    private final ILogService logService;

    @Autowired
    public DocumentoModeloEquipoService(IDocumentoModeloRepository documentoModeloRepository, ILogService logService) {
        this.documentoModeloRepository = documentoModeloRepository;
        this.logService = logService;
    }

    @Override
    public DocumentoModeloEquipo save(DocumentoModeloEquipo doc) {
        DocumentoModeloEquipo docum;
        try{
            docum = documentoModeloRepository.save(doc);
            logService.guardarLog("Se ha guardado el documento " + docum.getNombreArchivo() + " para el modelo " + docum.getModeloEquipo().getModelo(), true);
        }catch (Exception ex){
            docum = null;
        }

        return docum;
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
        try {
            documentoModeloRepository.delete(doc);
            logService.guardarLog("Se ha eliminado el documento " + doc.getNombreArchivo() + " para el modelo " + doc.getModeloEquipo().getModelo(), true);
        }catch (Exception ex){

        }
    }
}
