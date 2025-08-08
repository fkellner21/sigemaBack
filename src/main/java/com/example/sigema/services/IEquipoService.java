package com.example.sigema.services;

import com.example.sigema.models.Equipo;
import com.example.sigema.models.EquipoActas;
import com.example.sigema.models.ReporteActa;
import com.example.sigema.utilidades.SigemaException;
import jakarta.servlet.http.HttpServletResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public interface IEquipoService {

    List<Equipo> obtenerTodos(Long idUnidad) throws Exception;

    EquipoActas Crear(Equipo equipo) throws Exception;

    EquipoActas Eliminar(Long id) throws Exception;

    Equipo ObtenerPorId(Long id) throws Exception;

    EquipoActas Editar(Long id, Equipo equipo) throws Exception;

    List<Equipo> obtenerEquiposPorIdModelo(Long idModelo, Long idUnidad);
    void GenerarExcelIndicadoresGestion(HttpServletResponse response, Long idUnidad) throws SigemaException;
    ReporteActa generarActaEquipo(Equipo equipo, boolean esDotacion) throws IOException;
    void generarExcelInformeAnioProximo(HttpServletResponse response, Long idUnidad) throws SigemaException;

}
