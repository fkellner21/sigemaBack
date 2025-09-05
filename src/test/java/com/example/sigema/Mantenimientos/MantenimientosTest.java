package com.example.sigema.Mantenimientos;

import com.example.sigema.models.*;
import com.example.sigema.models.enums.UnidadMedida;
import com.example.sigema.repositories.IMantenimientoRepository;
import com.example.sigema.services.IEquipoService;
import com.example.sigema.services.IRepuestoService;
import com.example.sigema.services.implementations.MantenimientoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MantenimientosTest {

    @Mock
    private IMantenimientoRepository mantenimientoRepository;

    @Mock
    private IEquipoService equipoService;

    @Mock
    private IRepuestoService repuestoService;

    @InjectMocks
    private MantenimientoService mantenimientoService;

    private Equipo equipoMock;
    private Repuesto repuestoMock;
    private Mantenimiento mantenimientoMock;
    private MantenimientoDTO mantenimientoDTOMock;

    @BeforeEach
    void setUp() {
        // Setup Equipo mock
        equipoMock = new Equipo();
        equipoMock.setId(1L);
        equipoMock.setMatricula("Equipo Test");

        // Setup Repuesto mock
        repuestoMock = new Repuesto();
        repuestoMock.setId(1L);
        repuestoMock.setNombre("Repuesto Test");

        // Setup RepuestoMantenimiento mock
        RepuestoMantenimiento repuestoMantenimiento = new RepuestoMantenimiento();
        repuestoMantenimiento.setIdRepuesto(1L);
        repuestoMantenimiento.setCantidadUsada(2);

        // Setup MantenimientoDTO mock
        mantenimientoDTOMock = new MantenimientoDTO();
        mantenimientoDTOMock.setIdEquipo(1L);
        mantenimientoDTOMock.setFechaMantenimiento("2024-01-15");
        mantenimientoDTOMock.setDescripcion("Mantenimiento de prueba");
        mantenimientoDTOMock.setEsService(true);
        mantenimientoDTOMock.setUnidadMedida(UnidadMedida.HT);
        mantenimientoDTOMock.setCantidadUnidadMedida(2.5);
        mantenimientoDTOMock.setRepuestosMantenimiento(Arrays.asList(repuestoMantenimiento));

        // Setup Mantenimiento mock
        mantenimientoMock = new Mantenimiento();
        mantenimientoMock.setId(1L);
        mantenimientoMock.setEquipo(equipoMock);
        mantenimientoMock.setDescripcion("Mantenimiento existente");
        mantenimientoMock.setEsService(false);
    }

    @Test
    void obtenerTodos_DeberiaRetornarListaDeMantenimientos() throws Exception {
        // Arrange
        List<Mantenimiento> mantenimientosEsperados = Arrays.asList(mantenimientoMock);
        when(mantenimientoRepository.findAll()).thenReturn(mantenimientosEsperados);

        // Act
        List<Mantenimiento> resultado = mantenimientoService.obtenerTodos();

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(mantenimientoMock, resultado.get(0));
        verify(mantenimientoRepository, times(1)).findAll();
    }

    @Test
    void obtenerPorId_ConIdValido_DeberiaRetornarMantenimiento() throws Exception {
        // Arrange
        Long id = 1L;
        when(mantenimientoRepository.findById(id)).thenReturn(Optional.of(mantenimientoMock));

        // Act
        Optional<Mantenimiento> resultado = mantenimientoService.obtenerPorId(id);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(mantenimientoMock, resultado.get());
        verify(mantenimientoRepository, times(1)).findById(id);
    }

    @Test
    void obtenerPorId_ConIdInvalido_DeberiaRetornarEmpty() throws Exception {
        // Arrange
        Long id = 999L;
        when(mantenimientoRepository.findById(id)).thenReturn(Optional.empty());

        // Act
        Optional<Mantenimiento> resultado = mantenimientoService.obtenerPorId(id);

        // Assert
        assertFalse(resultado.isPresent());
        verify(mantenimientoRepository, times(1)).findById(id);
    }

    @Test
    void crear_ConDatosValidos_DeberiaCrearMantenimiento() throws Exception {
        // Arrange
        when(equipoService.ObtenerPorId(1L)).thenReturn(equipoMock);
        when(repuestoService.ObtenerPorId(1L)).thenReturn(Optional.of(repuestoMock));
        when(mantenimientoRepository.save(any(Mantenimiento.class))).thenReturn(mantenimientoMock);

        // Act
        mantenimientoDTOMock.setCantidadUnidadMedida(0);
        Mantenimiento resultado = mantenimientoService.crear(mantenimientoDTOMock);

        // Assert
        assertNotNull(resultado);
        verify(equipoService, times(1)).ObtenerPorId(1L);
        verify(repuestoService, times(1)).ObtenerPorId(1L);
        verify(mantenimientoRepository, times(1)).save(any(Mantenimiento.class));
    }

    @Test
    void crear_ConEquipoInexistente_DeberiaLanzarExcepcion() throws Exception {
        // Arrange
        when(equipoService.ObtenerPorId(1L)).thenReturn(null);

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            mantenimientoService.crear(mantenimientoDTOMock);
        });

        assertEquals("El equipo no existe", exception.getMessage());
        verify(equipoService, times(1)).ObtenerPorId(1L);
        verify(mantenimientoRepository, never()).save(any(Mantenimiento.class));
    }

    @Test
    void crear_ConRepuestoInexistente_DeberiaLanzarExcepcion() throws Exception {
        // Arrange
        when(equipoService.ObtenerPorId(1L)).thenReturn(equipoMock);
        when(repuestoService.ObtenerPorId(1L)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            mantenimientoService.crear(mantenimientoDTOMock);
        });

        assertEquals("Repuesto no encontrado", exception.getMessage());
        verify(equipoService, times(1)).ObtenerPorId(1L);
        verify(repuestoService, times(1)).ObtenerPorId(1L);
        verify(mantenimientoRepository, never()).save(any(Mantenimiento.class));
    }

    @Test
    void editar_ConIdExistente_DeberiaActualizarMantenimiento() throws Exception {
        // Arrange
        Long id = 1L;
        mantenimientoMock.setRepuestosMantenimiento(new ArrayList<>());
        mantenimientoMock.setCantidadUnidadMedida(2000);
        equipoMock.setCantidadUnidadMedida(2200);

        when(mantenimientoRepository.findById(id)).thenReturn(Optional.of(mantenimientoMock));
        when(equipoService.ObtenerPorId(1L)).thenReturn(equipoMock);
        when(repuestoService.ObtenerPorId(1L)).thenReturn(Optional.of(repuestoMock));
        when(mantenimientoRepository.save(any(Mantenimiento.class))).thenReturn(mantenimientoMock);

        // Act
        mantenimientoDTOMock.setCantidadUnidadMedida(1900);
        Mantenimiento resultado = mantenimientoService.editar(id, mantenimientoDTOMock);

        // Assert
        assertNotNull(resultado);
        verify(mantenimientoRepository, times(1)).findById(id);
        verify(equipoService, times(1)).ObtenerPorId(1L);
        verify(mantenimientoRepository, times(1)).save(any(Mantenimiento.class));
    }

    @Test
    void editar_ConIdInexistente_DeberiaRetornarNull() throws Exception {
        // Arrange
        Long id = 999L;
        when(mantenimientoRepository.findById(id)).thenReturn(Optional.empty());

        // Act
        Mantenimiento resultado = mantenimientoService.editar(id, mantenimientoDTOMock);

        // Assert
        assertNull(resultado);
        verify(mantenimientoRepository, times(1)).findById(id);
        verify(mantenimientoRepository, never()).save(any(Mantenimiento.class));
    }

    @Test
    void editar_ConEquipoInexistente_DeberiaLanzarExcepcion() throws Exception {
        // Arrange
        Long id = 1L;
        when(mantenimientoRepository.findById(id)).thenReturn(Optional.of(mantenimientoMock));
        when(equipoService.ObtenerPorId(1L)).thenReturn(null);

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            mantenimientoService.editar(id, mantenimientoDTOMock);
        });

        assertEquals("El equipo no existe", exception.getMessage());
        verify(mantenimientoRepository, times(1)).findById(id);
        verify(equipoService, times(1)).ObtenerPorId(1L);
        verify(mantenimientoRepository, never()).save(any(Mantenimiento.class));
    }

    @Test
    void eliminar_DeberiaLlamarDeleteById() throws Exception {
        // Arrange
        Long id = 1L;

        // Act
        mantenimientoService.eliminar(id);

        // Assert
        verify(mantenimientoRepository, times(1)).deleteById(id);
    }

    @Test
    void obtenerPorEquipo_DeberiaRetornarMantenimientosDelEquipo() throws Exception {
        // Arrange
        Long idEquipo = 1L;
        List<Mantenimiento> mantenimientosEsperados = Arrays.asList(mantenimientoMock);
        when(mantenimientoRepository.findByEquipo_IdOrderByFechaMantenimientoDesc(idEquipo))
                .thenReturn(mantenimientosEsperados);

        // Act
        List<Mantenimiento> resultado = mantenimientoService.obtenerPorEquipo(idEquipo);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(mantenimientoMock, resultado.get(0));
        verify(mantenimientoRepository, times(1))
                .findByEquipo_IdOrderByFechaMantenimientoDesc(idEquipo);
    }
}