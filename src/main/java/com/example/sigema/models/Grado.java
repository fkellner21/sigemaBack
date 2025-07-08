package com.example.sigema.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Table(name = "Grados")
@Getter
@Setter
public class Grado implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;

    public Grado() {
    }

    public Grado(Long id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }
}