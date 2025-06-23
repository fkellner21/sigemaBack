package com.example.sigema.models;

import lombok.Data;

@Data
public class JWTResponse {
    private String token;
    private String rol;
    private Long idUnidad;
    private Long idUsuario;

    public Long getIdUnidad() {
        return idUnidad;
    }

    public void setIdUnidad(Long idUnidad) {
        this.idUnidad = idUnidad;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public JWTResponse(String token, String rol, Long idUnidad, Long idUsuario) {
        this.token = token;
        this.rol = rol;
        this.idUnidad = idUnidad;
        this.idUsuario = idUsuario;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
}