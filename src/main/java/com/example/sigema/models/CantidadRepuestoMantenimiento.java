package com.example.sigema.models;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

//@Entity
//@Table(name = "CantidadRepuestosMantenimientos")
@Getter
@Setter
public class CantidadRepuestoMantenimiento implements Serializable {

    //Chequear id compuesto TODO
    @Column(nullable = false)
    private Long idMantenimiento;

    @Column(nullable = false)
    private Long idRepuesto;

    @Column(nullable = false)
    private double cantidad;
}