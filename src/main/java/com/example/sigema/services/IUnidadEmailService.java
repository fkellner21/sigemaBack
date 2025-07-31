package com.example.sigema.services;

import com.example.sigema.models.UnidadEmail;

import java.util.List;

public interface IUnidadEmailService {
    UnidadEmail agregarEmail(Long unidadId, String email) throws Exception;
    void eliminarEmail(Long emailId) throws Exception;
    List<UnidadEmail> obtenerEmailsPorUnidad(Long unidadId) throws Exception;

}