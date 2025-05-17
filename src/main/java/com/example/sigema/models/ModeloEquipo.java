package com.example.sigema.models;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;

@Entity
@Table(name = "ModelosEquipos")
public class ModeloEquipo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int anio;

    @Column(nullable = false)
    private String modelo;

    @Column(nullable = false)
    private double capacidad;

    @Column(nullable = false)
    private Long idMarca;

    private ArrayList<Long> idEquipos;

    private ArrayList<Long> idRepuestos;
}