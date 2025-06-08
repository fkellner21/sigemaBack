package com.example.sigema.Documentos;

import com.example.sigema.models.DocumentoModeloEquipo;
import com.example.sigema.models.ModeloEquipo;
import com.example.sigema.repositories.IDocumentoModeloRepository;
import com.example.sigema.services.implementations.DocumentoModeloEquipoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DocumentosTest {
    @Mock
    private IDocumentoModeloRepository documentoModeloRepository;

    @InjectMocks
    private DocumentoModeloEquipoService documentoModeloEquipoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSave() {
        DocumentoModeloEquipo doc = new DocumentoModeloEquipo();
        doc.setId(1L);
        doc.setNombreArchivo("manual.pdf");
        doc.setRutaArchivo("/docs/manual.pdf");
        doc.setFechaSubida(LocalDate.now());

        when(documentoModeloRepository.save(doc)).thenReturn(doc);

        DocumentoModeloEquipo resultado = documentoModeloEquipoService.save(doc);

        assertEquals("manual.pdf", resultado.getNombreArchivo());
        assertEquals("/docs/manual.pdf", resultado.getRutaArchivo());
        verify(documentoModeloRepository).save(doc);
    }

    @Test
    void testFindById_Existe() {
        DocumentoModeloEquipo doc = new DocumentoModeloEquipo();
        doc.setId(1L);
        doc.setNombreArchivo("manual.pdf");

        when(documentoModeloRepository.findById(1L)).thenReturn(Optional.of(doc));

        Optional<DocumentoModeloEquipo> resultado = documentoModeloEquipoService.findById(1L);

        assertTrue(resultado.isPresent());
        assertEquals("manual.pdf", resultado.get().getNombreArchivo());
    }

    @Test
    void testFindById_NoExiste() {
        when(documentoModeloRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<DocumentoModeloEquipo> resultado = documentoModeloEquipoService.findById(99L);

        assertFalse(resultado.isPresent());
    }

    @Test
    void testFindByModeloEquipo() {
        ModeloEquipo modelo = new ModeloEquipo();
        modelo.setId(1L);
        modelo.setModelo("Modelo A");
        DocumentoModeloEquipo doc1 = new DocumentoModeloEquipo();
        doc1.setId(1L);
        doc1.setNombreArchivo("doc1.pdf");
        doc1.setModeloEquipo(modelo);

        DocumentoModeloEquipo doc2 = new DocumentoModeloEquipo();
        doc2.setId(2L);
        doc2.setNombreArchivo("doc2.pdf");
        doc2.setModeloEquipo(modelo);

        List<DocumentoModeloEquipo> docs = Arrays.asList(doc1, doc2);

        when(documentoModeloRepository.findByModeloEquipo(modelo)).thenReturn(docs);

        List<DocumentoModeloEquipo> resultado = documentoModeloEquipoService.findByModeloEquipo(modelo);

        assertEquals(2, resultado.size());
        assertEquals("doc1.pdf", resultado.get(0).getNombreArchivo());
        assertEquals("doc2.pdf", resultado.get(1).getNombreArchivo());
        verify(documentoModeloRepository).findByModeloEquipo(modelo);
    }

    @Test
    void testDelete() {
        DocumentoModeloEquipo doc = new DocumentoModeloEquipo();
        doc.setId(1L);

        doNothing().when(documentoModeloRepository).delete(doc);

        documentoModeloEquipoService.delete(doc);

        verify(documentoModeloRepository).delete(doc);
    }
}
