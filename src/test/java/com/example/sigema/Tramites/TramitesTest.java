//package com.example.sigema.Tramites;
//
//import com.example.sigema.models.*;
//import com.example.sigema.models.enums.EstadoTramite;
//import com.example.sigema.models.enums.Rol;
//import com.example.sigema.models.enums.TipoNotificacion;
//import com.example.sigema.models.enums.TipoTramite;
//import com.example.sigema.repositories.ITramitesRepository;
//import com.example.sigema.services.*;
//import com.example.sigema.services.implementations.TramiteService;
//import com.example.sigema.utilidades.SigemaException;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.time.Instant;
//import java.time.LocalDate;
//import java.time.ZoneId;
//import java.util.*;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class TramiteServiceTest {
//
//    @Mock
//    private ITramitesRepository tramitesRepository;
//
//    @Mock
//    private IUnidadService unidadService;
//
//    @Mock
//    private IUsuarioService usuarioService;
//
//    @Mock
//    private IEquipoService equipoService;
//
//    @Mock
//    private IRepuestoService repuestoService;
//
//    @Mock
//    private INotificacionesService notificacionesService;
//
//    @InjectMocks
//    private TramiteService tramiteService;
//
//    private Usuario usuarioMock;
//    private Unidad unidadOrigenMock;
//    private Unidad unidadDestinoMock;
//    private Tramite tramiteMock;
//    private TramiteDTO tramiteDTOMock;
//    private Equipo equipoMock;
//    private Repuesto repuestoMock;
//
//    @BeforeEach
//    void setUp() {
//        // Setup usuario mock
//        usuarioMock = new Usuario();
//        usuarioMock.setId(1L);
//        usuarioMock.setNombreCompleto("Usuario Test");
//        usuarioMock.setCedula("12345678");
//        usuarioMock.setRol(Rol.ADMINISTRADOR);
//
//        // Setup unidades mock
//        unidadOrigenMock = new Unidad();
//        unidadOrigenMock.setId(1L);
//        unidadOrigenMock.setNombre("Unidad Origen");
//
//        unidadDestinoMock = new Unidad();
//        unidadDestinoMock.setId(2L);
//        unidadDestinoMock.setNombre("Unidad Destino");
//
//        usuarioMock.setUnidad(unidadOrigenMock);
//
//        // Setup equipo mock
//        equipoMock = new Equipo();
//        equipoMock.setId(1L);
//
//        // Setup repuesto mock
//        repuestoMock = new Repuesto();
//        repuestoMock.setId(1L);
//
//        // Setup tramite mock
//        tramiteMock = new Tramite();
//        tramiteMock.setId(1L);
//        tramiteMock.setTipoTramite(TipoTramite.Otros);
//        tramiteMock.setEstado(EstadoTramite.Iniciado);
//        tramiteMock.setFechaInicio(Date.from(Instant.now()));
//        tramiteMock.setTexto("Tramite de prueba");
//        tramiteMock.setUsuario(usuarioMock);
//        tramiteMock.setUnidadOrigen(unidadOrigenMock);
//        tramiteMock.setUnidadDestino(unidadDestinoMock);
//
//        // Setup TramiteDTO mock
//        tramiteDTOMock = new TramiteDTO();
//        tramiteDTOMock.setTipoTramite(TipoTramite.Otros);
//        tramiteDTOMock.setTexto("Tramite de prueba");
//        tramiteDTOMock.setIdUnidadOrigen(1L);
//        tramiteDTOMock.setIdUnidadDestino(2L);
//    }
//
//    @Test
//    void obtenerTodos_ConIdUnidadNulo_DeberiaRetornarTodosTramites() throws Exception {
//        // Arrange
//        List<Tramite> tramitesEsperados = Arrays.asList(tramiteMock);
//        when(tramitesRepository.findAll()).thenReturn(tramitesEsperados);
//
//        // Act
//        List<Tramite> resultado = tramiteService.ObtenerTodos(null);
//
//        // Assert
//        assertEquals(tramitesEsperados, resultado);
//        verify(tramitesRepository).findAll();
//    }
//
//    @Test
//    void obtenerTodos_ConIdUnidad_DeberiaRetornarTramitesFiltrados() throws Exception {
//        // Arrange
//        Long idUnidad = 1L;
//        List<Tramite> tramitesOrigen = Arrays.asList(tramiteMock);
//        List<Tramite> tramitesDestino = Arrays.asList();
//        List<Tramite> tramitesDestinoNull = Arrays.asList();
//
//        when(tramitesRepository.findByUnidadOrigen_Id(idUnidad)).thenReturn(tramitesOrigen);
//        when(tramitesRepository.findByUnidadDestino_Id(idUnidad)).thenReturn(tramitesDestino);
//        when(tramitesRepository.findByUnidadDestino_Id(null)).thenReturn(tramitesDestinoNull);
//
//        // Act
//        List<Tramite> resultado = tramiteService.ObtenerTodos(idUnidad);
//
//        // Assert
//        assertEquals(1, resultado.size());
//        assertTrue(resultado.contains(tramiteMock));
//    }
//
//    @Test
//    void crear_TramiteGeneral_DeberiaCrearTramiteCorrectamente() throws Exception {
//        // Arrange
//        Long idUsuario = 1L;
//        when(usuarioService.ObtenerPorId(idUsuario)).thenReturn(usuarioMock);
//        when(unidadService.ObtenerPorId(1L)).thenReturn(Optional.of(unidadOrigenMock));
//        when(unidadService.ObtenerPorId(2L)).thenReturn(Optional.of(unidadDestinoMock));
//        when(tramitesRepository.save(any(Tramite.class))).thenReturn(tramiteMock);
//
//        // Act
//        Tramite resultado = tramiteService.Crear(tramiteDTOMock, idUsuario);
//
//        // Assert
//        assertNotNull(resultado);
//        verify(tramitesRepository).save(any(Tramite.class));
//        verify(usuarioService, atLeastOnce()).ObtenerPorId(idUsuario);
//    }
//
//    @Test
//    void crear_AltaUsuarioConTramitePendiente_DeberiaLanzarExcepcion() throws Exception {
//        // Arrange
//        tramiteDTOMock.setTipoTramite(TipoTramite.AltaUsuario);
//        tramiteDTOMock.setCedulaUsuarioSolicitado("12345678");
//
//        Tramite tramiteExistente = new Tramite();
//        tramiteExistente.setEstado(EstadoTramite.Iniciado);
//        tramiteExistente.setCedulaUsuarioSolicitado("12345678");
//
//        when(tramitesRepository.findAll()).thenReturn(Arrays.asList(tramiteExistente));
//
//        // Act & Assert
//        SigemaException exception = assertThrows(SigemaException.class,
//                () -> tramiteService.Crear(tramiteDTOMock, 1L));
//
//        assertEquals("Hay tramites pendientes para el usuario solicitado", exception.getMessage());
//    }
//
//    @Test
//    void obtenerPorId_TramiteExistente_DeberiaRetornarTramite() throws Exception {
//        // Arrange
//        Long idTramite = 1L;
//        when(tramitesRepository.findById(idTramite)).thenReturn(Optional.of(tramiteMock));
//        when(tramitesRepository.save(any(Tramite.class))).thenReturn(tramiteMock);
//
//        // Act
//        Optional<Tramite> resultado = tramiteService.ObtenerPorId(idTramite, usuarioMock);
//
//        // Assert
//        assertTrue(resultado.isPresent());
//        assertEquals(tramiteMock, resultado.get());
//    }
//
//    @Test
//    void obtenerPorId_TramiteNoExistente() throws Exception {
//        // Arrange
//        Long idTramite = 999L;
//        when(tramitesRepository.findById(idTramite)).thenReturn(Optional.empty());
//
//        // Act & Assert
//        SigemaException exception = assertThrows(SigemaException.class,
//                () -> tramiteService.ObtenerPorId(idTramite, usuarioMock));
//        // Assert
//        assertEquals("Trámite no encontrado", exception.getMessage());
//    }
//
//    @Test
//    void editar_TramiteIniciado_DeberiaEditarCorrectamente() throws Exception {
//        // Arrange
//        Long idTramite = 1L;
//        Long idUsuario = 1L;
//        tramiteMock.setEstado(EstadoTramite.Iniciado);
//
//        when(tramitesRepository.findById(idTramite)).thenReturn(Optional.of(tramiteMock));
//        when(usuarioService.ObtenerPorId(idUsuario)).thenReturn(usuarioMock);
//        when(unidadService.ObtenerPorId(1L)).thenReturn(Optional.of(unidadOrigenMock));
//        when(unidadService.ObtenerPorId(2L)).thenReturn(Optional.of(unidadDestinoMock));
//        when(tramitesRepository.save(any(Tramite.class))).thenReturn(tramiteMock);
//
//        // Act
//        Tramite resultado = tramiteService.Editar(idTramite, tramiteDTOMock, idUsuario);
//
//        // Assert
//        assertNotNull(resultado);
//        verify(tramitesRepository).save(any(Tramite.class));
//    }
//
//    @Test
//    void editar_TramiteEnTramite_DeberiaLanzarExcepcion() throws Exception {
//        // Arrange
//        Long idTramite = 1L;
//        tramiteMock.setEstado(EstadoTramite.EnTramite);
//
//        when(tramitesRepository.findById(idTramite)).thenReturn(Optional.of(tramiteMock));
//
//        // Act & Assert
//        SigemaException exception = assertThrows(SigemaException.class,
//                () -> tramiteService.Editar(idTramite, tramiteDTOMock, 1L));
//
//        assertEquals("Su trámite ya fue procesado y no se puede editar", exception.getMessage());
//    }
//
//    @Test
//    void eliminar_TramiteExistente_DeberiaEliminarCorrectamente() throws Exception {
//        // Arrange
//        Long idTramite = 1L;
//        when(tramitesRepository.findById(idTramite)).thenReturn(Optional.of(tramiteMock));
//
//        // Act
//        assertDoesNotThrow(() -> tramiteService.Eliminar(idTramite));
//
//        // Assert
//        verify(tramitesRepository).delete(tramiteMock);
//    }
//
//    @Test
//    void eliminar_TramiteNoExistente_DeberiaLanzarExcepcion() throws Exception {
//        // Arrange
//        Long idTramite = 999L;
//        when(tramitesRepository.findById(idTramite)).thenReturn(Optional.empty());
//
//        // Act & Assert
//        SigemaException exception = assertThrows(SigemaException.class,
//                () -> tramiteService.Eliminar(idTramite));
//
//        assertEquals("El tramite no fue encontrado", exception.getMessage());
//    }
//
//    @Test
//    void crearActuacion_TramiteExistente_DeberiaCrearActuacion() throws Exception {
//        // Arrange
//        Long idTramite = 1L;
//        Long idUsuario = 1L;
//        Actuacion actuacion = new Actuacion();
//        actuacion.setDescripcion("Nueva actuacion");
//        actuacion.setFecha(Date.from(Instant.now()));
//
//        when(tramitesRepository.findById(idTramite)).thenReturn(Optional.of(tramiteMock));
//        when(usuarioService.ObtenerPorId(idUsuario)).thenReturn(usuarioMock);
//        when(tramitesRepository.save(any(Tramite.class))).thenReturn(tramiteMock);
//
//        // Act
//        Actuacion resultado = tramiteService.CrearActuacion(idTramite, actuacion, idUsuario);
//
//        // Assert
//        assertNotNull(resultado);
//        assertEquals(tramiteMock, resultado.getTramite());
//        assertEquals(usuarioMock, resultado.getUsuario());
//        verify(tramitesRepository).save(tramiteMock);
//    }
//
//    @Test
//    void crearActuacion_TramiteAprobado_DeberiaLanzarExcepcion() throws Exception {
//        // Arrange
//        Long idTramite = 1L;
//        tramiteMock.setEstado(EstadoTramite.Aprobado);
//        Actuacion actuacion = new Actuacion();
//
//        when(tramitesRepository.findById(idTramite)).thenReturn(Optional.of(tramiteMock));
//
//        // Act & Assert
//        SigemaException exception = assertThrows(SigemaException.class,
//                () -> tramiteService.CrearActuacion(idTramite, actuacion, 1L));
//
//        assertEquals("Nos se puede crear la actuación porque el trámite ya está resuelto", exception.getMessage());
//    }
//
//    @Test
//    void cambiarEstado_AAprobadoBajaEquipo_DeberiaEliminarEquipo() throws Exception {
//        // Arrange
//        Long idTramite = 1L;
//        Long idUsuario = 1L;
//        tramiteMock.setTipoTramite(TipoTramite.BajaEquipo);
//        tramiteMock.setEstado(EstadoTramite.EnTramite);
//        tramiteMock.setEquipo(equipoMock);
//
//        when(tramitesRepository.findById(idTramite)).thenReturn(Optional.of(tramiteMock));
//        when(usuarioService.ObtenerPorId(idUsuario)).thenReturn(usuarioMock);
//        when(tramitesRepository.save(any(Tramite.class))).thenReturn(tramiteMock);
//
//        // Act
//        Tramite resultado = tramiteService.CambiarEstado(idTramite, EstadoTramite.Aprobado, idUsuario);
//
//        // Assert
//        assertEquals(EstadoTramite.Aprobado, resultado.getEstado());
//        verify(equipoService).Eliminar(equipoMock.getId());
//        verify(tramitesRepository).save(tramiteMock);
//    }
//
//    @Test
//    void cambiarEstado_AAprobadoAltaUsuario_DeberiaCrearUsuario() throws Exception {
//        // Arrange
//        Long idTramite = 1L;
//        Long idUsuario = 1L;
//        tramiteMock.setTipoTramite(TipoTramite.AltaUsuario);
//        tramiteMock.setEstado(EstadoTramite.EnTramite);
//        tramiteMock.setCedulaUsuarioSolicitado("87654321");
//        tramiteMock.setNombreCompletoUsuarioSolicitado("Nuevo Usuario");
//        tramiteMock.setIdGradoUsuarioSolicitado(1L);
//        tramiteMock.setIdUnidadUsuarioSolicitado(1L);
//        tramiteMock.setTelefonoUsuarioSolicitado(123456789L);
//        tramiteMock.setRolSolicitado(Rol.UNIDAD);
//
//        when(tramitesRepository.findById(idTramite)).thenReturn(Optional.of(tramiteMock));
//        when(usuarioService.ObtenerPorId(idUsuario)).thenReturn(usuarioMock);
//        when(tramitesRepository.save(any(Tramite.class))).thenReturn(tramiteMock);
//
//        // Act
//        Tramite resultado = tramiteService.CambiarEstado(idTramite, EstadoTramite.Aprobado, idUsuario);
//
//        // Assert
//        assertEquals(EstadoTramite.Aprobado, resultado.getEstado());
//        verify(usuarioService).Crear(any(Usuario.class));
//        verify(tramitesRepository).save(tramiteMock);
//    }
//
//    @Test
//    void cambiarEstado_EstadoIgual_DeberiaLanzarExcepcion() throws Exception {
//        // Arrange
//        Long idTramite = 1L;
//        tramiteMock.setEstado(EstadoTramite.Iniciado);
//
//        when(tramitesRepository.findById(idTramite)).thenReturn(Optional.of(tramiteMock));
//
//        // Act & Assert
//        SigemaException exception = assertThrows(SigemaException.class,
//                () -> tramiteService.CambiarEstado(idTramite, EstadoTramite.Iniciado, 1L));
//
//        assertEquals("El tramite ya se encuentra en el estado seleccionado", exception.getMessage());
//    }
//
//    @Test
//    void cambiarEstado_ReabrirTramiteAprobado_DeberiaLanzarExcepcion() throws Exception {
//        // Arrange
//        Long idTramite = 1L;
//        Unidad unidad = new Unidad();
//        unidad.setId(1L);
//        tramiteMock.setEstado(EstadoTramite.Aprobado);
//        tramiteMock.setUnidadDestino(unidad);
//
//        when(tramitesRepository.findById(idTramite)).thenReturn(Optional.of(tramiteMock));
//        when(usuarioService.ObtenerPorId(1L)).thenReturn(usuarioMock);
//
//        // Act & Assert
//        SigemaException exception = assertThrows(SigemaException.class,
//                () -> tramiteService.CambiarEstado(idTramite, EstadoTramite.EnTramite, 1L));
//
//        assertEquals("No se puede re abrir un trámite", exception.getMessage());
//    }
//
//    @Test
//    void obtenerTodosPorFechas_SinUnidad_DeberiaFiltrarPorFechas() throws Exception {
//        // Arrange
//        LocalDate fechaDesde = LocalDate.of(2024, 1, 1);
//        LocalDate fechaHasta = LocalDate.of(2024, 12, 31);
//        Date dateDesde = Date.from(fechaDesde.atStartOfDay(ZoneId.of("America/Montevideo")).toInstant());
//        Date dateHasta = Date.from(fechaHasta.atTime(23, 59, 59).atZone(ZoneId.of("America/Montevideo")).toInstant());
//
//        List<Tramite> tramitesEsperados = Arrays.asList(tramiteMock);
//        when(tramitesRepository.findByFechaInicioBetween(any(Date.class), any(Date.class)))
//                .thenReturn(tramitesEsperados);
//
//        // Act
//        List<Tramite> resultado = tramiteService.ObtenerTodosPorFechas(null, dateDesde, dateHasta);
//
//        // Assert
//        assertEquals(tramitesEsperados, resultado);
//        verify(tramitesRepository).findByFechaInicioBetween(any(Date.class), any(Date.class));
//    }
//
//    @Test
//    void crear_UsuarioNoEncontrado_DeberiaLanzarExcepcion() throws Exception {
//        // Arrange
//        when(usuarioService.ObtenerPorId(1L)).thenReturn(null);
//
//        // Act & Assert
//        SigemaException exception = assertThrows(SigemaException.class,
//                () -> tramiteService.Crear(tramiteDTOMock, 1L));
//
//        assertEquals("El usuario no fue encontrado", exception.getMessage());
//    }
//}