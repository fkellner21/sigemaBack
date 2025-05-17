package com.example.sigema.models;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "Actuaciones")
public class Actuacion implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long idTramite;

    @Column(nullable = false)
    private Long idUsuario;

    @Column(nullable = false)
    private Long idUnidadDestino;

    @Column(nullable = false)
    private String descripcion;

    @Column(nullable = false)
    private Date fecha;
}