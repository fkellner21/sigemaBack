package com.example.sigema.models;

import com.example.sigema.models.enums.UnidadMedida;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "Mantenimientos")
@Getter
@Setter
public class Mantenimiento implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String descripcion;

    @Column(nullable = false)
    private Date fecha;

    @Column(nullable = false)
    private Long idEquipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UnidadMedida unidadMedida;

    @Column(nullable = false)
    private double cantidadUnidadMedida;
}