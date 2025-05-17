package com.example.sigema.models;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "TiposEquipos")
public class TipoEquipo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;
}
