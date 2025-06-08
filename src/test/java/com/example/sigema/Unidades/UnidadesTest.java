package com.example.sigema.Unidades;

import com.example.sigema.models.Unidad;
import com.example.sigema.repositories.IUnidadRepository;
import com.example.sigema.services.implementations.UnidadService;
import com.example.sigema.utilidades.SigemaException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UnidadesTest {
    @Mock
    private IUnidadRepository unidadRepository;

    @InjectMocks
    private UnidadService unidadService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testObtenerTodos() throws Exception {
        List<Unidad> unidades = Arrays.asList(new Unidad(), new Unidad());
        when(unidadRepository.findAll()).thenReturn(unidades);

        List<Unidad> resultado = unidadService.obtenerTodos();

        assertEquals(2, resultado.size());
        verify(unidadRepository).findAll();
    }

    @Test
    void testCrear_UnidadValida() throws Exception {
        Unidad unidad = new Unidad();
        unidad.setNombre("Metro");

        when(unidadRepository.save(unidad)).thenReturn(unidad);

        Unidad resultado = unidadService.Crear(unidad);

        assertEquals("Metro", resultado.getNombre());
        verify(unidadRepository).save(unidad);
    }

    @Test
    void testCrear_UnidadInvalida_NombreVacio() {
        Unidad unidad = new Unidad();
        unidad.setNombre(""); // Nombre vacío

        SigemaException exception = assertThrows(SigemaException.class, () -> {
            unidadService.Crear(unidad);
        });

        assertEquals("Debes ingresar un nombre", exception.getMessage());
        verify(unidadRepository, never()).save(any());
    }

    @Test
    void testEliminar() throws Exception {
        Long id = 1L;

        unidadService.Eliminar(id);

        verify(unidadRepository).deleteById(id);
    }

    @Test
    void testObtenerPorId_Encontrado() throws Exception {
        Long id = 1L;
        Unidad unidad = new Unidad();
        unidad.setId(id);
        unidad.setNombre("Metro");

        when(unidadRepository.findById(id)).thenReturn(Optional.of(unidad));

        Optional<Unidad> resultado = unidadService.ObtenerPorId(id);

        assertTrue(resultado.isPresent());
        assertEquals("Metro", resultado.get().getNombre());
    }

    @Test
    void testObtenerPorId_NoEncontrado() throws Exception {
        Long id = 2L;
        when(unidadRepository.findById(id)).thenReturn(Optional.empty());

        Optional<Unidad> resultado = unidadService.ObtenerPorId(id);

        assertFalse(resultado.isPresent());
    }

    @Test
    void testEditar_UnidadValida() throws Exception {
        Long id = 1L;
        Unidad existente = new Unidad();
        existente.setId(id);
        existente.setNombre("Metro");

        Unidad nueva = new Unidad();
        nueva.setNombre("Kilogramo");

        when(unidadRepository.findById(id)).thenReturn(Optional.of(existente));
        when(unidadRepository.save(any(Unidad.class))).thenReturn(existente);

        Unidad resultado = unidadService.Editar(id, nueva);

        assertEquals("Kilogramo", resultado.getNombre());
        verify(unidadRepository).save(existente);
    }

    @Test
    void testEditar_UnidadNoExiste() {
        Long id = 99L;
        Unidad nueva = new Unidad();
        nueva.setNombre("Litro");

        when(unidadRepository.findById(id)).thenReturn(Optional.empty());

        SigemaException exception = assertThrows(SigemaException.class, () -> {
            unidadService.Editar(id, nueva);
        });

        assertEquals("Unidad no encontrada", exception.getMessage());
        verify(unidadRepository, never()).save(any());
    }

    @Test
    void testEditar_UnidadInvalida_NombreVacio() {
        Long id = 1L;
        Unidad existente = new Unidad();
        existente.setId(id);
        existente.setNombre("Metro");

        Unidad nueva = new Unidad();
        nueva.setNombre("");

        when(unidadRepository.findById(id)).thenReturn(Optional.of(existente));

        SigemaException exception = assertThrows(SigemaException.class, () -> {
            unidadService.Editar(id, nueva);
        });

        assertEquals("Debes ingresar un nombre", exception.getMessage());
        verify(unidadRepository, never()).save(any());
    }
}