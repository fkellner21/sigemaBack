package com.example.sigema.Repuestos;

import com.example.sigema.models.Repuesto;
import com.example.sigema.models.enums.TipoRepuesto;
import com.example.sigema.repositories.IRepuestoRepository;
import com.example.sigema.services.implementations.RepuestoService;
import com.example.sigema.utilidades.SigemaException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RepuestosTest {

    private IRepuestoRepository repuestoRepository;
    private RepuestoService repuestoService;

    @BeforeEach
    void setup() {
        repuestoRepository = mock(IRepuestoRepository.class);
        repuestoService = new RepuestoService(repuestoRepository);
    }

    // Helper para crear un repuesto válido
    private Repuesto crearRepuestoValido() {
        Repuesto r = new Repuesto();
        r.setIdModelo(1L);
        r.setNombre("Filtro");
        r.setCodigoSICE("SICE001");
        r.setTipo(TipoRepuesto.Pieza);
        r.setCantidad(10);
        return r;
    }

    // Crear repuesto válido - éxito
    @Test
    void crearRepuesto_Exitoso() throws Exception {
        Repuesto nuevo = crearRepuestoValido();

        when(repuestoRepository.findByCodigoSICE("SICE001")).thenReturn(Optional.empty());
        when(repuestoRepository.findByNombre("Filtro")).thenReturn(Optional.empty());
        when(repuestoRepository.save(nuevo)).thenReturn(nuevo);

        Repuesto creado = repuestoService.Crear(nuevo);

        assertEquals("Filtro", creado.getNombre());
        verify(repuestoRepository).save(nuevo);
    }

    // Crear repuesto con validación: idModelo null -> excepción
    @Test
    void crearRepuesto_Validacion_IdModeloNulo() {
        Repuesto r = crearRepuestoValido();
        r.setIdModelo(null);

        SigemaException ex = assertThrows(SigemaException.class, () -> repuestoService.Crear(r));
        assertEquals("Debe ingresar el modelo", ex.getMessage());
        verify(repuestoRepository, never()).save(any());
    }

    // Crear repuesto con validación: nombre vacío -> excepción
    @Test
    void crearRepuesto_Validacion_NombreVacio() {
        Repuesto r = crearRepuestoValido();
        r.setNombre("");

        SigemaException ex = assertThrows(SigemaException.class, () -> repuestoService.Crear(r));
        assertEquals("Debe ingresar el nombre", ex.getMessage());
        verify(repuestoRepository, never()).save(any());
    }

    // Crear repuesto con validación: codigoSICE vacío -> excepción
    @Test
    void crearRepuesto_Validacion_CodigoSICEVacio() {
        Repuesto r = crearRepuestoValido();
        r.setCodigoSICE(null);

        SigemaException ex = assertThrows(SigemaException.class, () -> repuestoService.Crear(r));
        assertEquals("Debe ingresar el código SICE", ex.getMessage());
        verify(repuestoRepository, never()).save(any());
    }

    // Crear repuesto con validación: cantidad <= 0 -> excepción
    @Test
    void crearRepuesto_Validacion_CantidadInvalida() {
        Repuesto r = crearRepuestoValido();
        r.setCantidad(0);

        SigemaException ex = assertThrows(SigemaException.class, () -> repuestoService.Crear(r));
        assertEquals("Debe ingresar la cantidad", ex.getMessage());
        verify(repuestoRepository, never()).save(any());
    }

    // Crear repuesto con validación: tipo no Pieza ni Lubricante -> excepción
    @Test
    void crearRepuesto_Validacion_TipoInvalido() {
        Repuesto r = crearRepuestoValido();
        r.setTipo(null); // o cualquier otro valor no permitido

        SigemaException ex = assertThrows(SigemaException.class, () -> repuestoService.Crear(r));
        assertEquals("El tipo de repuesto debe ser Pieza o Lubricante", ex.getMessage());
        verify(repuestoRepository, never()).save(any());
    }

    // Crear repuesto con codigoSICE ya existente -> excepción
    @Test
    void crearRepuesto_CodigoSICEExistente() throws Exception {
        Repuesto r = crearRepuestoValido();

        when(repuestoRepository.findByCodigoSICE("SICE001")).thenReturn(Optional.of(new Repuesto()));

        SigemaException ex = assertThrows(SigemaException.class, () -> repuestoService.Crear(r));
        assertEquals("Ya existe un repuesto con ese código SICE", ex.getMessage());
        verify(repuestoRepository, never()).save(any());
    }

    // Crear repuesto con nombre ya existente -> excepción
    @Test
    void crearRepuesto_NombreExistente() throws Exception {
        Repuesto r = crearRepuestoValido();

        when(repuestoRepository.findByCodigoSICE("SICE001")).thenReturn(Optional.empty());
        when(repuestoRepository.findByNombre("Filtro")).thenReturn(Optional.of(new Repuesto()));

        SigemaException ex = assertThrows(SigemaException.class, () -> repuestoService.Crear(r));
        assertEquals("Ya existe un repuesto con ese nombre", ex.getMessage());
        verify(repuestoRepository, never()).save(any());
    }

    // Editar repuesto exitoso
    @Test
    void editarRepuesto_Exitoso() throws Exception {
        Repuesto existente = crearRepuestoValido();
        existente.setId(1L);

        Repuesto modificado = crearRepuestoValido();
        modificado.setNombre("Filtro Modificado");
        modificado.setCodigoSICE("SICE002");

        when(repuestoRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(repuestoRepository.findByCodigoSICE("SICE002")).thenReturn(Optional.empty());
        when(repuestoRepository.findByNombre("Filtro Modificado")).thenReturn(Optional.empty());
        when(repuestoRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        Repuesto resultado = repuestoService.Editar(1L, modificado);

        assertEquals("Filtro Modificado", resultado.getNombre());
        assertEquals("SICE002", resultado.getCodigoSICE());
        verify(repuestoRepository).save(existente);
    }

    // Editar repuesto no encontrado -> excepción
    @Test
    void editarRepuesto_NoEncontrado() {
        Repuesto modificado = crearRepuestoValido();

        when(repuestoRepository.findById(99L)).thenReturn(Optional.empty());

        Exception ex = assertThrows(Exception.class, () -> repuestoService.Editar(99L, modificado));
        assertEquals("Repuesto con ID 99 no encontrado", ex.getMessage());
    }

    // Editar repuesto con codigoSICE repetido -> excepción
    @Test
    void editarRepuesto_CodigoSICEExistente() throws Exception {
        Repuesto existente = crearRepuestoValido();
        existente.setId(1L);

        Repuesto modificado = crearRepuestoValido();
        modificado.setCodigoSICE("SICE_DUP");

        Repuesto otro = new Repuesto();
        otro.setId(2L);

        when(repuestoRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(repuestoRepository.findByCodigoSICE("SICE_DUP")).thenReturn(Optional.of(otro));

        SigemaException ex = assertThrows(SigemaException.class, () -> repuestoService.Editar(1L, modificado));
        assertEquals("Ya existe un repuesto con ese código SICE", ex.getMessage());
    }

    // Editar repuesto con nombre repetido -> excepción
    @Test
    void editarRepuesto_NombreExistente() throws Exception {
        Repuesto existente = crearRepuestoValido();
        existente.setId(1L);

        Repuesto modificado = crearRepuestoValido();
        modificado.setNombre("NombreExistente");

        Repuesto otro = new Repuesto();
        otro.setId(2L);

        when(repuestoRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(repuestoRepository.findByCodigoSICE("SICE001")).thenReturn(Optional.empty());
        when(repuestoRepository.findByNombre("NombreExistente")).thenReturn(Optional.of(otro));

        SigemaException ex = assertThrows(SigemaException.class, () -> repuestoService.Editar(1L, modificado));
        assertEquals("Ya existe un repuesto con ese nombre", ex.getMessage());
    }

}