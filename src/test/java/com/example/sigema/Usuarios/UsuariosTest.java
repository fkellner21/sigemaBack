package com.example.sigema.Usuarios;

import com.example.sigema.SecurityConfig;
import com.example.sigema.models.Grado;
import com.example.sigema.models.Unidad;
import com.example.sigema.models.Usuario;
import com.example.sigema.models.enums.Rol;
import com.example.sigema.repositories.IRepositoryUsuario;
import com.example.sigema.services.IGradoService;
import com.example.sigema.services.IUnidadService;
import com.example.sigema.services.implementations.UsuarioService;
import com.example.sigema.utilidades.SigemaException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UsuariosTest {

    @Mock
    private IRepositoryUsuario usuarioRepository;

    @Mock
    private IUnidadService unidadService;

    @Mock
    private IGradoService gradoService;

    @Mock
    private SecurityConfig securityConfig;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(securityConfig.passwordEncoder()).thenReturn(passwordEncoder);
    }

    @Test
    void crear_Usuario_Exitoso() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setIdUnidad(1L);
        usuario.setIdGrado(2L);
        usuario.setPassword("clave123");
        usuario.setNombreCompleto("Juan Pérez");

        Unidad unidad = new Unidad();
        Grado grado = new Grado();
        when(unidadService.ObtenerPorId(1L)).thenReturn(Optional.of(unidad));
        when(gradoService.ObtenerPorId(2L)).thenReturn(grado);
        when(passwordEncoder.encode("clave123")).thenReturn("hashedPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

        Usuario creado = usuarioService.Crear(usuario);

        assertEquals("hashedPassword", creado.getPassword());
        assertEquals(unidad, creado.getUnidad());
        assertEquals(grado, creado.getGrado());
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void crear_Usuario_SinPassword_LanzaExcepcion() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setPassword("");

        SigemaException ex = assertThrows(SigemaException.class, () -> {
            usuarioService.Crear(usuario);
        });

        assertEquals("Debe ingresar una contraseña", ex.getMessage());
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void obtenerTodos_Usuarios_Exitoso() throws Exception {
        List<Usuario> lista = new ArrayList<>();
        lista.add(new Usuario());
        lista.add(new Usuario());

        when(usuarioRepository.findAll()).thenReturn(lista);

        List<Usuario> resultado = usuarioService.obtenerTodos();

        assertEquals(2, resultado.size());
        verify(usuarioRepository).findAll();
    }

    @Test
    void obtenerPorId_UsuarioExistente() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setId(1L);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        Usuario resultado = usuarioService.ObtenerPorId(1L);

        assertEquals(1L, resultado.getId());
        verify(usuarioRepository).findById(1L);
    }

    @Test
    void obtenerPorId_UsuarioNoExiste_LanzaExcepcion() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> {
            usuarioService.ObtenerPorId(1L);
        });

        verify(usuarioRepository).findById(1L);
    }

    @Test
    void eliminar_Usuario_Exitoso() throws Exception {
        doNothing().when(usuarioRepository).deleteById(1L);

        usuarioService.Eliminar(1L);

        verify(usuarioRepository).deleteById(1L);
    }

    @Test
    void editar_Usuario_Exitoso() throws Exception {
        Usuario usuarioInput = new Usuario();
        usuarioInput.setIdUnidad(1L);
        usuarioInput.setIdGrado(2L);
        usuarioInput.setNombreCompleto("Nuevo Nombre");
        usuarioInput.setRol(Rol.ADMINISTRADOR);
        usuarioInput.setPassword("nuevaClave");

        Usuario usuarioExistente = new Usuario();
        usuarioExistente.setId(1L);

        Unidad unidad = new Unidad();
        Grado grado = new Grado();

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioExistente));
        when(unidadService.ObtenerPorId(1L)).thenReturn(Optional.of(unidad));
        when(gradoService.ObtenerPorId(2L)).thenReturn(grado);
        when(passwordEncoder.encode("nuevaClave")).thenReturn("hashedNuevaClave");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

        Usuario resultado = usuarioService.Editar(1L, usuarioInput);

        assertEquals("Nuevo Nombre", resultado.getNombreCompleto());
        assertEquals(Rol.ADMINISTRADOR, resultado.getRol());
        assertEquals("hashedNuevaClave", resultado.getPassword());
        assertEquals(unidad, resultado.getUnidad());
        assertEquals(grado, resultado.getGrado());

        verify(usuarioRepository).save(usuarioExistente);
    }

    @Test
    void editar_UsuarioNoExiste_LanzaExcepcion() {
        Usuario usuarioInput = new Usuario();

        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        Exception ex = assertThrows(Exception.class, () -> {
            usuarioService.Editar(1L, usuarioInput);
        });

        assertEquals("No existe el usuario con el id 1", ex.getMessage());
    }
}