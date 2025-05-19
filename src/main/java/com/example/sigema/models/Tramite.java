package com.example.sigema.models;

import com.example.sigema.models.enums.EstadoTramite;
import com.example.sigema.models.enums.TipoTramite;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

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
    private TipoTramite tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoTramite estado = EstadoTramite.Iniciado;

    @Column(nullable = false)
    private Date fechaInicio;

    @Column(nullable = false)
    private Long idUnidadOrigen;

    @Column(nullable = false)
    private Long idUnidadDestino;

    @Column
    private Long idEquipo;

    @Column
    private Long idRepuesto;

    @Column(nullable = false)
    private Long idUsuario;
}