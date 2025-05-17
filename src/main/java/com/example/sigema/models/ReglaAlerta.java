package com.example.sigema.models;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "ReglasAlertas")
public class ReglaAlerta implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;

    @Column(nullable = false)
    private int intervaloMeses = 6;

    @Column(nullable = false)
    private int cantidadUnidadMedida;

    @Column(nullable = false)
    private Long idTipoMantenimiento;
}