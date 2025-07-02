package com.example.sigema.models;

import com.example.sigema.models.enums.TipoTramite;

public class TramiteDTO {

    private TipoTramite tipoTramite;

    private Long idUnidadOrigen;

    private Long idUnidadDestino;

    private Long idEquipo;

    private Long idRepuesto;

    private String texto;

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public TipoTramite getTipoTramite() {
        return tipoTramite;
    }

    public void setTipoTramite(TipoTramite tipoTramite) {
        this.tipoTramite = tipoTramite;
    }

    public Long getIdUnidadOrigen() {
        return idUnidadOrigen;
    }

    public void setIdUnidadOrigen(Long idUnidadOrigen) {
        this.idUnidadOrigen = idUnidadOrigen;
    }

    public Long getIdUnidadDestino() {
        return idUnidadDestino;
    }

    public void setIdUnidadDestino(Long idUnidadDestino) {
        this.idUnidadDestino = idUnidadDestino;
    }

    public Long getIdEquipo() {
        return idEquipo;
    }

    public void setIdEquipo(Long idEquipo) {
        this.idEquipo = idEquipo;
    }

    public Long getIdRepuesto() {
        return idRepuesto;
    }

    public void setIdRepuesto(Long idRepuesto) {
        this.idRepuesto = idRepuesto;
    }

}