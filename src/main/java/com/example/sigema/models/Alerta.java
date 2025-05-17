package com.example.sigema.models;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "Alertas")
public class Alerta implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long idEquipo;

    @Column(nullable = false)
    private Long idUsuarioDestino;

    @Column(nullable = false)
    private String descripcion;

    @Column(nullable = false)
    private Date fecha;
}