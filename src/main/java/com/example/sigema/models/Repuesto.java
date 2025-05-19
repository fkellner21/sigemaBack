package com.example.sigema.models;

import com.example.sigema.models.enums.TipoRepuesto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Table(name = "Repuestos")
@Getter
@Setter
public class Repuesto implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long idModelo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoRepuesto tipo;

    @Column(nullable = false)
    private String nombre;

    @Column
    private String caracteristicas;

    @Column(nullable = false)
    private double cantidad;

    @Column
    private String observaciones;

    @Column(unique = true)
    private String codigoSICE;
}
