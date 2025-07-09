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
@Table(name = "VisualizacionesTramite")
@Getter
@Setter
public class VisualizacionTramite implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "tramite_id", referencedColumnName = "id")
    @JsonIgnore
    private Tramite tramite;

    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id", referencedColumnName = "id")
    private Usuario usuario;

    @Column
    private Date fecha;

    @Column
    private String descripcion;

    @Transient
    @JsonProperty("tramiteId")
    public Long getTramiteId() {
        return tramite != null ? tramite.getId() : null;
    }
}