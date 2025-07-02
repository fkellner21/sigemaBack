package com.example.sigema.models;

import com.example.sigema.models.enums.EstadoTramite;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
@Entity
@Table(name = "EstadosHistoricoTramite")
@Getter
@Setter
public class EstadosHistoricoTramite implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con Tramite: se usa para persistencia
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tramite_id", referencedColumnName = "id")
    @JsonIgnore // evita que se envíe el objeto completo al frontend
    private Tramite tramite;

    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id", referencedColumnName = "id")
    private Usuario usuario;

    @Column
    private EstadoTramite estado;

    @Column
    private Date fecha;

    // Exponer solo el id del tramite en JSON
    @Transient
    @JsonProperty("tramiteId")
    public Long getTramiteId() {
        return tramite != null ? tramite.getId() : null;
    }

    // Getters y setters restantes...


    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public EstadoTramite getEstado() {
        return estado;
    }

    public void setEstado(EstadoTramite estado) {
        this.estado = estado;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

}