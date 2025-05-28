package com.example.sigema.TiposEquipos;

import com.example.sigema.models.TipoEquipo;
import com.example.sigema.repositories.ITiposEquiposRepository;
import com.example.sigema.services.implementations.TiposEquiposService;
import com.example.sigema.utilidades.SigemaException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TiposEquiposTest {
    @Mock
    private ITiposEquiposRepository tiposEquiposRepository;

    @InjectMocks
    private TiposEquiposService tiposEquiposService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void crear_tipoEquipoValido_exitoso() throws Exception {
        TipoEquipo tipoEquipo = new TipoEquipo();
        tipoEquipo.setCodigo("MAV01");
        tipoEquipo.setNombre("Retroexcavadora");
        tipoEquipo.setActivo(true);

        when(tiposEquiposRepository.findByCodigo("MAV01")).thenReturn(Optional.empty());
        when(tiposEquiposRepository.save(any(TipoEquipo.class))).thenAnswer(i -> i.getArgument(0));

        TipoEquipo resultado = tiposEquiposService.Crear(tipoEquipo);

        assertNotNull(resultado);
        assertEquals("MAV01", resultado.getCodigo());
        assertEquals("Retroexcavadora", resultado.getNombre());
        verify(tiposEquiposRepository).save(tipoEquipo);
    }

    @Test
    public void crear_tipoEquipoSinNombre_lanzaException() {
        TipoEquipo tipoEquipo = new TipoEquipo();
        tipoEquipo.setCodigo("MAV02");
        tipoEquipo.setNombre(""); // nombre vacío
        tipoEquipo.setActivo(true);

        Exception exception = assertThrows(SigemaException.class, () -> {
            tiposEquiposService.Crear(tipoEquipo);
        });

        assertEquals("Debe ingresar un nombre", exception.getMessage());
        verify(tiposEquiposRepository, never()).save(any());
    }

    @Test
    public void crear_tipoEquipoConCodigoExistente_lanzaException() {
        TipoEquipo tipoEquipo = new TipoEquipo();
        tipoEquipo.setCodigo("MAV03");
        tipoEquipo.setNombre("Compactadora");
        tipoEquipo.setActivo(true);

        TipoEquipo existente = new TipoEquipo();
        existente.setCodigo("MAV03");

        when(tiposEquiposRepository.findByCodigo("MAV03")).thenReturn(Optional.of(existente));

        Exception exception = assertThrows(SigemaException.class, () -> {
            tiposEquiposService.Crear(tipoEquipo);
        });

        assertEquals("Ya existe un tipo de equipo con el código MAV03", exception.getMessage());
        verify(tiposEquiposRepository, never()).save(any());
    }

    @Test
    public void editar_tipoEquipoValida_exitoso() throws Exception {
        Long id = 1L;

        TipoEquipo tipoEquipoNuevo = new TipoEquipo();
        tipoEquipoNuevo.setCodigo("MAV04");
        tipoEquipoNuevo.setNombre("Pavimentadora");
        tipoEquipoNuevo.setActivo(true);

        TipoEquipo tipoEquipoExistente = new TipoEquipo();
        tipoEquipoExistente.setCodigo("MAV04");
        tipoEquipoExistente.setNombre("Motoniveladora");
        tipoEquipoExistente.setActivo(false);

        when(tiposEquiposRepository.findByCodigo("MAV04")).thenReturn(Optional.of(tipoEquipoExistente));
        when(tiposEquiposRepository.findById(id)).thenReturn(Optional.of(tipoEquipoExistente));
        when(tiposEquiposRepository.save(any(TipoEquipo.class))).thenAnswer(i -> i.getArgument(0));

        TipoEquipo resultado = tiposEquiposService.Editar(id, tipoEquipoNuevo);

        assertEquals("Pavimentadora", resultado.getNombre());
        assertTrue(resultado.isActivo());
        verify(tiposEquiposRepository).save(tipoEquipoExistente);
    }

    @Test
    public void editar_tipoEquipoCodigoRepetido_lanzaException() {
        Long idEditar = 2L;

        TipoEquipo tipoEquipoNuevo = new TipoEquipo();
        tipoEquipoNuevo.setCodigo("MAV05");
        tipoEquipoNuevo.setNombre("Fresadora");
        tipoEquipoNuevo.setActivo(true);

        TipoEquipo otroTipoEquipo = new TipoEquipo();
        otroTipoEquipo.setId(1L);
        otroTipoEquipo.setCodigo("MAV05");

        TipoEquipo tipoEquipoExistente = new TipoEquipo();
        tipoEquipoExistente.setId(idEditar);
        tipoEquipoExistente.setCodigo("OLD01");
        tipoEquipoExistente.setNombre("Antiguo");
        tipoEquipoExistente.setActivo(true);

        when(tiposEquiposRepository.findByCodigo("MAV05")).thenReturn(Optional.of(otroTipoEquipo));
        when(tiposEquiposRepository.findById(idEditar)).thenReturn(Optional.of(tipoEquipoExistente));

        Exception exception = assertThrows(SigemaException.class, () -> {
            tiposEquiposService.Editar(idEditar, tipoEquipoNuevo);
        });

        assertEquals("Ya existe un tipo de equipo con el código MAV05", exception.getMessage());
        verify(tiposEquiposRepository, never()).save(any());
    }
}
