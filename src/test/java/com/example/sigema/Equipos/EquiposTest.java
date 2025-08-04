//package com.example.sigema.Equipos;
//
//import com.example.sigema.models.Equipo;
//import com.example.sigema.models.ModeloEquipo;
//import com.example.sigema.models.Unidad;
//import com.example.sigema.models.enums.EstadoEquipo;
//import com.example.sigema.models.enums.UnidadMedida;
//import com.example.sigema.repositories.IEquipoRepository;
//import com.example.sigema.services.IModeloEquipoService;
//import com.example.sigema.services.IUnidadService;
//import com.example.sigema.services.implementations.EquipoService;
//import com.example.sigema.utilidades.SigemaException;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//public class EquiposTest
//{
//    @InjectMocks
//    private EquipoService equipoService;
//
//    @Mock
//    private IEquipoRepository equipoRepository;
//
//    @Mock
//    private IModeloEquipoService modeloEquipoService;
//
//    @Mock
//    private IUnidadService unidadService;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    private Equipo crearEquipoValido() {
//        Equipo equipo = new Equipo();
//        equipo.setMatricula("ABC123");
////        equipo.setUnidadMedida(UnidadMedida.HT);
//        equipo.setCantidadUnidadMedida(100);
//        equipo.setEstado(EstadoEquipo.Verde);
//        equipo.setIdModeloEquipo(1L);
//        return equipo;
//    }
//
//    @Test
//    void testCrearEquipoValido() throws Exception {
//        Equipo equipo = crearEquipoValido();
//        ModeloEquipo modelo = new ModeloEquipo();
//        modelo.setId(1L);
//        Unidad unidad = new Unidad();
//        unidad.setId(1L);
//
//        equipo.setIdUnidad(1L);
//        equipo.setIdModeloEquipo(1L);
//
//        when(unidadService.ObtenerPorId(1L)).thenReturn(Optional.of(unidad));
//        when(modeloEquipoService.ObtenerPorId(1L)).thenReturn(Optional.of(modelo));
//        when(equipoRepository.save(any(Equipo.class))).thenAnswer(i -> i.getArgument(0));
//
//        Equipo resultado = equipoService.Crear(equipo);
//
//        assertNotNull(resultado);
//        assertEquals("ABC123", resultado.getMatricula());
//        verify(equipoRepository).save(equipo);
//    }
//
//    @Test
//    void testCrearEquipoConModeloInvalido() throws Exception {
//        Equipo equipo = crearEquipoValido();
//        Unidad unidad = new Unidad();
//        unidad.setId(1L);
//
//        equipo.setIdUnidad(1L);
//
//        when(unidadService.ObtenerPorId(1L)).thenReturn(Optional.of(unidad));
//        when(modeloEquipoService.ObtenerPorId(1L)).thenReturn(Optional.empty());
//
//        SigemaException ex = assertThrows(SigemaException.class, () -> equipoService.Crear(equipo));
//        assertEquals("El modelo de equipo ingresado no existe", ex.getMessage());
//    }
//
//    @Test
//    void testObtenerTodos() throws Exception {
//        when(equipoRepository.findAll()).thenReturn(List.of(new Equipo(), new Equipo()));
//        List<Equipo> lista = equipoService.obtenerTodos(null);
//        assertEquals(2, lista.size());
//    }
//
//    @Test
//    void testObtenerPorIdExistente() throws Exception {
//        Equipo equipo = crearEquipoValido();
//        equipo.setId(1L);
//
//        when(equipoRepository.findById(1L)).thenReturn(Optional.of(equipo));
//        Equipo resultado = equipoService.ObtenerPorId(1L);
//
//        assertNotNull(resultado);
//        assertEquals(1L, resultado.getId());
//    }
//
//    @Test
//    void testEliminar() throws Exception {
//        equipoService.Eliminar(1L);
//        verify(equipoRepository).deleteById(1L);
//    }
//
//    @Test
//    void testEditarEquipoExistente() throws Exception {
//        Equipo nuevo = crearEquipoValido();
//        ModeloEquipo modelo = new ModeloEquipo();
//        modelo.setId(1L);
//        Equipo existente = crearEquipoValido();
//        existente.setId(1L);
//        Unidad unidad = new Unidad();
//        unidad.setId(1L);
//
//        nuevo.setIdUnidad(1L);
//
//        when(unidadService.ObtenerPorId(1L)).thenReturn(Optional.of(unidad));
//        when(modeloEquipoService.ObtenerPorId(1L)).thenReturn(Optional.of(modelo));
//        when(equipoRepository.findById(1L)).thenReturn(Optional.of(existente));
//        when(equipoRepository.save(any(Equipo.class))).thenAnswer(i -> i.getArgument(0));
//
//        Equipo actualizado = equipoService.Editar(1L, nuevo);
//
//        assertNotNull(actualizado);
//        assertEquals("ABC123", actualizado.getMatricula());
//        verify(equipoRepository).save(existente);
//    }
//
//    @Test
//    void testEditarEquipoInexistente() throws Exception {
//        Equipo nuevo = crearEquipoValido();
//        ModeloEquipo modelo = new ModeloEquipo();
//        modelo.setId(1L);
//        Unidad unidad = new Unidad();
//        unidad.setId(1L);
//
//        nuevo.setIdUnidad(1L);
//
//        when(unidadService.ObtenerPorId(1L)).thenReturn(Optional.of(unidad));
//        when(modeloEquipoService.ObtenerPorId(1L)).thenReturn(Optional.of(modelo));
//        when(equipoRepository.findById(1L)).thenReturn(Optional.empty());
//
//        SigemaException ex = assertThrows(SigemaException.class, () -> equipoService.Editar(1L, nuevo));
//        assertEquals("El equipo no existe", ex.getMessage());
//    }
//}
