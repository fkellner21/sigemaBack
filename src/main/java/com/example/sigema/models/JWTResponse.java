package com.example.sigema.models;

import lombok.Data;

@Data
public class JWTResponse {
    private final String token;
    private final String rol;
    private final Long idUnidad;
    private final Long idUsuario;

    public JWTResponse(String token, String rol, Long idUnidad, Long idUsuario) {
        this.token = token;
        this.rol = rol;
        this.idUnidad = idUnidad;
        this.idUsuario = idUsuario;
    }
}