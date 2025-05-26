package com.example.sigema.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;

@Entity
@Table(name = "ModelosEquipos")
@Getter
@Setter
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
    @ManyToOne
    private Marca marca;

    private Long idMarca;

    @OneToMany(mappedBy = "modeloEquipo")
    private ArrayList<Equipo> equipos = new ArrayList<>();

    @ManyToMany
    @JoinTable(name="ModeloEquipoRepuesto",joinColumns = @JoinColumn(name = "modeloEquipoId"),inverseJoinColumns = @JoinColumn(name="repuestoId"))
    private ArrayList<Repuesto> repuestos = new ArrayList<>();

    public Long getIdMarca() {
        return idMarca;
    }

    public void setIdMarca(Long idMarca) {
        this.idMarca = idMarca;
    }
}