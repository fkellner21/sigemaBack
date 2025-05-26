package com.example.sigema.models;

import com.example.sigema.models.enums.EstadoEquipo;
import com.example.sigema.models.enums.UnidadMedida;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Table(name = "Equipos")
@Getter
@Setter
public class Equipo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String matricula;

    @Column
    private String observaciones;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UnidadMedida unidadMedida;

    @Column(nullable = false)
    private double cantidadUnidadMedida = 0;

    @Column
    private Long idUltimaPosicion;

    @ManyToOne
    @JoinColumn(name = "modelo_equipo_id")
    private ModeloEquipo modeloEquipo;

    public ModeloEquipo getModeloEquipo() {
        return modeloEquipo;
    }

    public void setModeloEquipo(ModeloEquipo modeloEquipo) {
        this.modeloEquipo = modeloEquipo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public UnidadMedida getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(UnidadMedida unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    public double getCantidadUnidadMedida() {
        return cantidadUnidadMedida;
    }

    public void setCantidadUnidadMedida(double cantidadUnidadMedida) {
        this.cantidadUnidadMedida = cantidadUnidadMedida;
    }

    public Long getIdUltimaPosicion() {
        return idUltimaPosicion;
    }

    public void setIdUltimaPosicion(Long idUltimaPosicion) {
        this.idUltimaPosicion = idUltimaPosicion;
    }

    public EstadoEquipo getEstado() {
        return estado;
    }

    public void setEstado(EstadoEquipo estado) {
        this.estado = estado;
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoEquipo estado;

    // private ArrayList<Long> idMantenimientos;
}