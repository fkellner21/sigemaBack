package com.example.sigema.Grados;

import com.example.sigema.models.Grado;
import com.example.sigema.repositories.IRepositoryGrado;
import com.example.sigema.services.implementations.GradoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GradosTest {

    @Mock
    private IRepositoryGrado gradoRepository;

    @InjectMocks
    private GradoService gradoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void crear_Grado_Exitoso() throws Exception {
        Grado nuevoGrado = new Grado();
        nuevoGrado.setNombre("Teniente");

        when(gradoRepository.save(any(Grado.class))).thenAnswer(i -> i.getArgument(0));

        Grado creado = gradoService.Crear(nuevoGrado);

        assertEquals("Teniente", creado.getNombre());
        verify(gradoRepository).save(nuevoGrado);
    }

    @Test
    void obtenerTodos_Grados_Exitoso() throws Exception {
        List<Grado> grados = new ArrayList<>();
        grados.add(new Grado(1L, "Capitán"));
        grados.add(new Grado(2L, "Mayor"));

        when(gradoRepository.findAll()).thenReturn(grados);

        List<Grado> resultado = gradoService.obtenerTodos();

        assertEquals(2, resultado.size());
        verify(gradoRepository).findAll();
    }

    @Test
    void obtenerPorId_Grado_Existente() throws Exception {
        Grado grado = new Grado(1L, "General");

        when(gradoRepository.findById(1L)).thenReturn(Optional.of(grado));

        Grado resultado = gradoService.ObtenerPorId(1L);

        assertEquals("General", resultado.getNombre());
        verify(gradoRepository).findById(1L);
    }

    @Test
    void obtenerPorId_Grado_NoExistente() {
        when(gradoRepository.findById(1L)).thenReturn(Optional.empty());

        Exception ex = assertThrows(Exception.class, () -> {
            gradoService.ObtenerPorId(1L);
        });

        assertEquals("No existe el grado con el id 1", ex.getMessage());
        verify(gradoRepository).findById(1L);
    }

    @Test
    void eliminar_Grado_Exitoso() throws Exception {
        doNothing().when(gradoRepository).deleteById(1L);

        gradoService.Eliminar(1L);

        verify(gradoRepository).deleteById(1L);
    }

    @Test
    void editar_Grado_Exitoso() throws Exception {
        Grado existente = new Grado(1L, "Sargento");
        Grado actualizado = new Grado();
        actualizado.setNombre("Suboficial");

        when(gradoRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(gradoRepository.save(any(Grado.class))).thenAnswer(i -> i.getArgument(0));

        Grado resultado = gradoService.Editar(1L, actualizado);

        assertEquals("Suboficial", resultado.getNombre());
        verify(gradoRepository).findById(1L);
        verify(gradoRepository).save(existente);
    }

    @Test
    void editar_Grado_NoExistente_LanzaExcepcion() {
        Grado actualizado = new Grado();
        actualizado.setNombre("Suboficial");

        when(gradoRepository.findById(1L)).thenReturn(Optional.empty());

        Exception ex = assertThrows(Exception.class, () -> {
            gradoService.Editar(1L, actualizado);
        });

        assertEquals("No existe el grado con el id 1", ex.getMessage());
        verify(gradoRepository).findById(1L);
        verify(gradoRepository, never()).save(any());
    }
}