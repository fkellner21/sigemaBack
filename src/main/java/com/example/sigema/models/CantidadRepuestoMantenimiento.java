package com.example.sigema.models;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "CantidadRepuestosMantenimientos")
public class CantidadRepuestoMantenimiento implements Serializable {

    //Chequear id compuesto TODO
    @Column(nullable = false)
    private Long idMantenimiento;

    @Column(nullable = false)
    private Long idRepuesto;

    @Column(nullable = false)
    private double cantidad;
}