package com.example.sigema.models;

import com.example.sigema.models.enums.EstadoTramite;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "EstadosHistoricoTramite")
@Getter
@Setter
public class EstadosHistoricoTramite implements Serializable {
    private EstadoTramite estado;
    private Date fecha;

    @ManyToOne(optional = false)
    @JoinColumn(name = "tramite_id", referencedColumnName = "id")
    @JsonBackReference
    private Tramite tramite;

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id", referencedColumnName = "id")
    private Usuario usuario;

    public Tramite getTramite() {
        return tramite;
    }

    public void setTramite(Tramite tramite) {
        this.tramite = tramite;
    }

    public EstadoTramite getEstado() {
        return estado;
    }

    public void setEstado(EstadoTramite estado) {
        estado = estado;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        fecha = fecha;
    }
}