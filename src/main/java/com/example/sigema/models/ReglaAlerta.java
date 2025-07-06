package com.example.sigema.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Table(name = "ReglasAlertas")
@Getter
@Setter
public class ReglaAlerta implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;

    @Column(nullable = false)
    private final int intervaloMeses = 6;

    @Column(nullable = false)
    private int cantidadUnidadMedida;

    @Column(nullable = false)
    private Long idTipoMantenimiento;
}