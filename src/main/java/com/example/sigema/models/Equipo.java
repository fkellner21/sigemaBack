package com.example.sigema.models;

import com.example.sigema.models.enums.EstadoEquipo;
import com.example.sigema.utilidades.SigemaException;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
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

    @Column(nullable = false)
    private double cantidadUnidadMedida = 0;

    @Column
    private Long idUltimaPosicion;

    @ManyToOne
    @JoinColumn(name = "modelo_equipo_id")
    private ModeloEquipo modeloEquipo;

    @Transient
    private Long idModeloEquipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoEquipo estado = EstadoEquipo.Verde;

    @ManyToOne
    @JoinColumn(name = "unidad_id", nullable = false)
    private Unidad unidad;

    @Transient
    private Long idUnidad;

    public Long getIdUnidad() {
        return idUnidad;
    }

    public void setIdUnidad(Long idUnidad) {
        this.idUnidad = idUnidad;
    }
// Getters y Setters
    public ModeloEquipo getModeloEquipo() {
        return modeloEquipo;
    }

    public void setModeloEquipo(ModeloEquipo modeloEquipo) {
        this.modeloEquipo = modeloEquipo;
    }

    public Long getIdModeloEquipo() {
        return idModeloEquipo;
    }

    public void setIdModeloEquipo(Long idModeloEquipo) {
        this.idModeloEquipo = idModeloEquipo;
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

    public Unidad getUnidad() {
        return unidad;
    }

    public void setUnidad(Unidad unidad) {
        this.unidad = unidad;
    }

    public void validar() throws SigemaException {
        if (matricula == null || matricula.isEmpty()) {
            throw new SigemaException("Debe ingresar una matricula");
        }

        if (cantidadUnidadMedida < 0) {
            throw new SigemaException("La cantidad de " + this.modeloEquipo.getUnidadMedida() + " no debe ser menor a 0");
        }

        if ((idModeloEquipo == null || idModeloEquipo == 0) &&
                (modeloEquipo == null || modeloEquipo.getId() == null || modeloEquipo.getId() == 0)) {
            throw new SigemaException("Debe ingresar un modelo de equipo");
        }

        if (estado != EstadoEquipo.Amarillo &&
                estado != EstadoEquipo.Negro &&
                estado != EstadoEquipo.Rojo &&
                estado != EstadoEquipo.Verde) {
            throw new SigemaException("El estado debe ser Verde, Amarillo, Rojo o Negro");
        }

        if (idUnidad == null || idUnidad == 0) {
            throw new SigemaException("Debe asociar una unidad válida al equipo");
        }
    }
}
