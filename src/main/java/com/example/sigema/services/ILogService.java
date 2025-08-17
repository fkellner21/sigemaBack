package com.example.sigema.services;


import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.List;

public interface ILogService {
    void guardarLog(String mensaje, boolean mostrarUsuario);
    Resource descargarLogPorFecha(String fecha) throws IOException;
    List<String> listarLogsDisponibles();
}