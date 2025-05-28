package com.example.sigema.Marcas;

import com.example.sigema.models.Marca;
import com.example.sigema.repositories.IMarcaRepository;
import com.example.sigema.services.implementations.MarcaService;
import com.example.sigema.utilidades.SigemaException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class MarcasTest {
    @Mock
    private IMarcaRepository marcaRepository;

    @InjectMocks
    private MarcaService marcaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void crear_Marca_Exitoso() throws Exception {
        Marca nuevaMarca = new Marca();
        nuevaMarca.setNombre("MarcaNueva");

        when(marcaRepository.findByNombre("MarcaNueva")).thenReturn(Optional.empty());
        when(marcaRepository.save(any(Marca.class))).thenAnswer(i -> i.getArgument(0));

        Marca creada = marcaService.Crear(nuevaMarca);

        assertEquals("MarcaNueva", creada.getNombre());
        verify(marcaRepository).findByNombre("MarcaNueva");
        verify(marcaRepository).save(nuevaMarca);
    }

    @Test
    void crear_Marca_SinNombre_LanzaExcepcion() {
        Marca marca = new Marca();
        marca.setNombre("");

        SigemaException ex = assertThrows(SigemaException.class, () -> {
            marcaService.Crear(marca);
        });
        assertEquals("Debe ingresar el nombre", ex.getMessage());

        verifyNoInteractions(marcaRepository);
    }

    @Test
    void crear_Marca_NombreRepetido_LanzaExcepcion() {
        Marca marcaExistente = new Marca();
        marcaExistente.setId(1L);
        marcaExistente.setNombre("MarcaExistente");

        Marca nuevaMarca = new Marca();
        nuevaMarca.setNombre("MarcaExistente");

        when(marcaRepository.findByNombre("MarcaExistente")).thenReturn(Optional.of(marcaExistente));

        SigemaException ex = assertThrows(SigemaException.class, () -> {
            marcaService.Crear(nuevaMarca);
        });
        assertEquals("Ya existe una marca con ese nombre", ex.getMessage());

        verify(marcaRepository).findByNombre("MarcaExistente");
        verify(marcaRepository, never()).save(any());
    }

    @Test
    void editar_Marca_Exitoso() throws Exception {
        Marca marcaExistente = new Marca();
        marcaExistente.setId(1L);
        marcaExistente.setNombre("MarcaVieja");

        Marca datosEditados = new Marca();
        datosEditados.setNombre("MarcaNueva");

        when(marcaRepository.findByNombre("MarcaNueva")).thenReturn(Optional.empty());
        when(marcaRepository.findById(1L)).thenReturn(Optional.of(marcaExistente));
        when(marcaRepository.save(any(Marca.class))).thenAnswer(i -> i.getArgument(0));

        Marca editada = marcaService.Editar(1L, datosEditados);

        assertEquals("MarcaNueva", editada.getNombre());
        verify(marcaRepository).findByNombre("MarcaNueva");
        verify(marcaRepository).findById(1L);
        verify(marcaRepository).save(marcaExistente);
    }

    @Test
    void editar_Marca_NombreRepetido_LanzaExcepcion() {
        Marca otraMarca = new Marca();
        otraMarca.setId(2L);
        otraMarca.setNombre("MarcaExistente");

        Marca datosEditados = new Marca();
        datosEditados.setNombre("MarcaExistente");

        when(marcaRepository.findByNombre("MarcaExistente")).thenReturn(Optional.of(otraMarca));

        SigemaException ex = assertThrows(SigemaException.class, () -> {
            marcaService.Editar(1L, datosEditados);
        });

        assertEquals("Ya existe una marca con ese nombre", ex.getMessage());

        verify(marcaRepository).findByNombre("MarcaExistente");
        verify(marcaRepository, never()).findById(any());
        verify(marcaRepository, never()).save(any());
    }

    @Test
    void editar_Marca_NoEncontrada_LanzaExcepcion() {
        Marca datosEditados = new Marca();
        datosEditados.setNombre("MarcaNueva");

        when(marcaRepository.findByNombre("MarcaNueva")).thenReturn(Optional.empty());
        when(marcaRepository.findById(1L)).thenReturn(Optional.empty());

        Exception ex = assertThrows(Exception.class, () -> {
            marcaService.Editar(1L, datosEditados);
        });

        assertEquals("Marca con ID 1 no encontrado", ex.getMessage());

        verify(marcaRepository).findByNombre("MarcaNueva");
        verify(marcaRepository).findById(1L);
        verify(marcaRepository, never()).save(any());
    }

    @Test
    void obtenerPorId_Encontrada() throws Exception {
        Marca marca = new Marca();
        marca.setId(1L);
        marca.setNombre("Marca");

        when(marcaRepository.findById(1L)).thenReturn(Optional.of(marca));

        Optional<Marca> resultado = marcaService.ObtenerPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals("Marca", resultado.get().getNombre());
        verify(marcaRepository).findById(1L);
    }

    @Test
    void obtenerPorId_NoEncontrada() throws Exception {
        when(marcaRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Marca> resultado = marcaService.ObtenerPorId(1L);

        assertFalse(resultado.isPresent());
        verify(marcaRepository).findById(1L);
    }

    @Test
    void obtenerTodos_Exitoso() {
        List<Marca> listaMarcas = new ArrayList<>();
        Marca m1 = new Marca();
        m1.setId(1L);
        m1.setNombre("M1");
        Marca m2 = new Marca();
        m2.setId(2L);
        m2.setNombre("M2");
        listaMarcas.add(m1);
        listaMarcas.add(m2);

        when(marcaRepository.findAll()).thenReturn(listaMarcas);

        List<Marca> resultado = marcaService.ObtenerTodos();

        assertEquals(2, resultado.size());
        verify(marcaRepository).findAll();
    }
}
