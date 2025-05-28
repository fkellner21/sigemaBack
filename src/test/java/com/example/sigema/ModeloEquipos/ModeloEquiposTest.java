package com.example.sigema.ModeloEquipos;

import com.example.sigema.models.Marca;
import com.example.sigema.models.ModeloEquipo;
import com.example.sigema.models.TipoEquipo;
import com.example.sigema.repositories.IModeloEquipoRepository;
import com.example.sigema.services.IMarcaService;
import com.example.sigema.services.ITiposEquiposService;
import com.example.sigema.services.implementations.ModeloEquipoService;
import com.example.sigema.utilidades.SigemaException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ModeloEquiposTest {
    @Mock
    private IModeloEquipoRepository modeloEquipoRepository;

    @Mock
    private IMarcaService marcaService;

    @Mock
    private ITiposEquiposService tiposEquiposService;

    @InjectMocks
    private ModeloEquipoService modeloEquipoService;

    private ModeloEquipo modeloEquipo;

    private Marca marca;

    private TipoEquipo tipoEquipo;

    @BeforeEach
    void setup() {
        modeloEquipo = new ModeloEquipo();
        modeloEquipo.setIdMarca(1L);
        modeloEquipo.setIdTipoEquipo(2L);
        modeloEquipo.setAnio(2023);
        modeloEquipo.setModelo("Modelo X");
        modeloEquipo.setCapacidad(100.0);

        marca = new Marca();
        marca.setId(1L);

        tipoEquipo = new TipoEquipo();
        tipoEquipo.setId(2L);
    }

    @Test
    void crear_ModeloEquipo_Success() throws Exception {
        when(marcaService.ObtenerPorId(1L)).thenReturn(Optional.of(marca));
        when(tiposEquiposService.ObtenerPorId(2L)).thenReturn(Optional.of(tipoEquipo));
        when(modeloEquipoRepository.save(any(ModeloEquipo.class))).thenAnswer(i -> i.getArgument(0));

        ModeloEquipo resultado = modeloEquipoService.Crear(modeloEquipo);

        assertNotNull(resultado);
        assertEquals(marca, resultado.getMarca());
        assertEquals(tipoEquipo, resultado.getTipoEquipo());

        verify(marcaService).ObtenerPorId(1L);
        verify(tiposEquiposService).ObtenerPorId(2L);
        verify(modeloEquipoRepository).save(any(ModeloEquipo.class));
    }

    @Test
    void crear_ModeloEquipo_MarcaNoEncontrada() throws Exception {
        when(marcaService.ObtenerPorId(1L)).thenReturn(Optional.empty());
        when(tiposEquiposService.ObtenerPorId(2L)).thenReturn(Optional.of(tipoEquipo));

        SigemaException ex = assertThrows(SigemaException.class, () -> {
            modeloEquipoService.Crear(modeloEquipo);
        });

        assertEquals("Marca con ID 1 no encontrado", ex.getMessage());
        verify(marcaService).ObtenerPorId(1L);
        verify(tiposEquiposService).ObtenerPorId(2L);
        verify(modeloEquipoRepository, never()).save(any());
    }

    @Test
    void crear_ModeloEquipo_TipoEquipoNoEncontrado() throws Exception {
        when(marcaService.ObtenerPorId(1L)).thenReturn(Optional.of(marca));
        when(tiposEquiposService.ObtenerPorId(2L)).thenReturn(Optional.empty());

        SigemaException ex = assertThrows(SigemaException.class, () -> {
            modeloEquipoService.Crear(modeloEquipo);
        });

        assertEquals("Tipo de Equipo con ID 2 no encontrado", ex.getMessage());
        verify(marcaService).ObtenerPorId(1L);
        verify(tiposEquiposService).ObtenerPorId(2L);
        verify(modeloEquipoRepository, never()).save(any());
    }

    @Test
    void crear_ModeloEquipo_ValidacionFalla() {
        modeloEquipo.setAnio(1800);

        SigemaException ex = assertThrows(SigemaException.class, () -> {
            modeloEquipoService.Crear(modeloEquipo);
        });

        assertTrue(ex.getMessage().contains("El año debe ser mayor o igual a 1900"));

        verifyNoInteractions(marcaService);
        verifyNoInteractions(tiposEquiposService);
        verifyNoInteractions(modeloEquipoRepository);
    }

    @Test
    void editar_ModeloEquipo_Success() throws Exception {
        ModeloEquipo modeloExistente = new ModeloEquipo();
        modeloExistente.setId(10L);

        when(modeloEquipoRepository.findById(10L)).thenReturn(Optional.of(modeloExistente));
        when(marcaService.ObtenerPorId(1L)).thenReturn(Optional.of(marca));
        when(tiposEquiposService.ObtenerPorId(2L)).thenReturn(Optional.of(tipoEquipo));
        when(modeloEquipoRepository.save(any(ModeloEquipo.class))).thenAnswer(i -> i.getArgument(0));

        ModeloEquipo resultado = modeloEquipoService.Editar(10L, modeloEquipo);

        assertNotNull(resultado);
        assertEquals(marca, resultado.getMarca());
        assertEquals(tipoEquipo, resultado.getTipoEquipo());
        assertEquals(modeloEquipo.getAnio(), resultado.getAnio());
        assertEquals(modeloEquipo.getModelo(), resultado.getModelo());
        assertEquals(modeloEquipo.getCapacidad(), resultado.getCapacidad());

        verify(modeloEquipoRepository).findById(10L);
        verify(marcaService).ObtenerPorId(1L);
        verify(tiposEquiposService).ObtenerPorId(2L);
        verify(modeloEquipoRepository).save(any(ModeloEquipo.class));
    }

    @Test
    void editar_ModeloEquipo_NoEncontrado() throws Exception {
        when(modeloEquipoRepository.findById(10L)).thenReturn(Optional.empty());
        when(marcaService.ObtenerPorId(anyLong())).thenReturn(Optional.of(new Marca()));
        when(tiposEquiposService.ObtenerPorId(anyLong())).thenReturn(Optional.of(new TipoEquipo()));

        SigemaException ex = assertThrows(SigemaException.class, () -> {
            modeloEquipoService.Editar(10L, modeloEquipo);
        });

        assertEquals("Modelo con ID 10 no encontrado", ex.getMessage());

        verify(modeloEquipoRepository).findById(10L);
        verifyNoMoreInteractions(modeloEquipoRepository);
        verify(marcaService, atLeastOnce()).ObtenerPorId(anyLong());
        verify(tiposEquiposService, atLeastOnce()).ObtenerPorId(anyLong());
    }

    @Test
    void editar_ModeloEquipo_MarcaNoEncontrada() throws Exception {
        ModeloEquipo modeloExistente = new ModeloEquipo();
        modeloExistente.setId(10L);

        when(modeloEquipoRepository.findById(10L)).thenReturn(Optional.of(modeloExistente));
        when(marcaService.ObtenerPorId(1L)).thenReturn(Optional.empty());
        when(tiposEquiposService.ObtenerPorId(anyLong())).thenReturn(Optional.of(new TipoEquipo()));

        SigemaException ex = assertThrows(SigemaException.class, () -> {
            modeloEquipoService.Editar(10L, modeloEquipo);
        });

        assertEquals("Marca con ID 1 no encontrado", ex.getMessage());

        verify(modeloEquipoRepository).findById(10L);
        verify(marcaService).ObtenerPorId(1L);
        verify(tiposEquiposService).ObtenerPorId(anyLong());
        verify(modeloEquipoRepository, never()).save(any());
    }

    @Test
    void editar_ModeloEquipo_TipoEquipoNoEncontrado() throws Exception {
        ModeloEquipo modeloExistente = new ModeloEquipo();
        modeloExistente.setId(10L);

        when(modeloEquipoRepository.findById(10L)).thenReturn(Optional.of(modeloExistente));
        when(marcaService.ObtenerPorId(1L)).thenReturn(Optional.of(marca));
        when(tiposEquiposService.ObtenerPorId(2L)).thenReturn(Optional.empty());

        SigemaException ex = assertThrows(SigemaException.class, () -> {
            modeloEquipoService.Editar(10L, modeloEquipo);
        });

        assertEquals("Tipo de Equipo con ID 2 no encontrado", ex.getMessage());

        verify(modeloEquipoRepository).findById(10L);
        verify(marcaService).ObtenerPorId(1L);
        verify(tiposEquiposService).ObtenerPorId(2L);
        verify(modeloEquipoRepository, never()).save(any());
    }

    @Test
    void obtenerPorId_Success() throws Exception {
        ModeloEquipo modeloExistente = new ModeloEquipo();
        modeloExistente.setId(10L);

        when(modeloEquipoRepository.findById(10L)).thenReturn(Optional.of(modeloExistente));

        Optional<ModeloEquipo> resultado = modeloEquipoService.ObtenerPorId(10L);

        assertTrue(resultado.isPresent());
        assertEquals(modeloExistente, resultado.get());

        verify(modeloEquipoRepository).findById(10L);
    }

    @Test
    void obtenerTodos_Success() {
        when(modeloEquipoRepository.findAll()).thenReturn(List.of(modeloEquipo));

        List<ModeloEquipo> resultado = modeloEquipoService.ObtenerTodos();

        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        assertEquals(modeloEquipo, resultado.get(0));

        verify(modeloEquipoRepository).findAll();
    }
}