package com.example.sigema.models;

import com.example.sigema.models.enums.EstadoEquipo;
import com.example.sigema.utilidades.SigemaException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

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

    @Column
    private String numeroMotor;

    @Column(nullable = false)
    private double cantidadUnidadMedida = 0;

    @Column(nullable = false)
    private double latitud;

    @Column(nullable = false)
    private double longitud;

    @Column(nullable = false)
    private Date fechaUltimaPosicion = new Date();

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

    @Column(nullable = false)
    private boolean activo = false;

    @OneToMany(mappedBy = "equipo", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Mantenimiento> mantenimientos;

    public void validar() throws SigemaException {

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

        if ((idUnidad == null || idUnidad == 0) && (unidad == null || unidad.getId() == null || unidad.getId() == 0)) {
            throw new SigemaException("Debe asociar una unidad válida al equipo");
        }
    }
}
