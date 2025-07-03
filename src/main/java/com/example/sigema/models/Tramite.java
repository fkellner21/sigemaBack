package com.example.sigema.models;

import com.example.sigema.models.enums.EstadoTramite;
import com.example.sigema.models.enums.TipoTramite;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "Tramites")
@Getter
@Setter
public class Tramite implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoTramite tipoTramite;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private final EstadoTramite estado = EstadoTramite.Iniciado;

    @Column(nullable = false)
    private Date fechaInicio;

    @Column
    private String texto;

    @ManyToOne(optional = false)
    @JoinColumn(name = "unidad_origen_id", referencedColumnName = "id")
    private Unidad unidadOrigen;

    @ManyToOne
    @JoinColumn(name = "unidad_destino_id", referencedColumnName = "id")
    private Unidad unidadDestino;

    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id", referencedColumnName = "id")
    private Usuario usuario;
    @ManyToOne
    @JoinColumn(name = "repuesto_id", referencedColumnName = "id")
    private Repuesto repuesto;

    @OneToMany(mappedBy = "tramite", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private final List<EstadosHistoricoTramite> historico = new ArrayList<>();

    @OneToMany(mappedBy = "tramite", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private final List<Actuacion> actuaciones = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "equipo_id", referencedColumnName = "id")
    private Equipo equipo;
}