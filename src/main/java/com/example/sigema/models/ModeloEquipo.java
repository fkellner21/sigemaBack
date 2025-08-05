package com.example.sigema.models;

import com.example.sigema.models.enums.UnidadMedida;
import com.example.sigema.utilidades.SigemaException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@JsonInclude(JsonInclude.Include.NON_NULL)
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

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_marca", referencedColumnName = "id", nullable = false)
    private Marca marca;

    @Transient
    private Long idMarca;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_tipo_equipo", referencedColumnName = "id", nullable = false)
    private TipoEquipo tipoEquipo;

    @Transient
    private Long idTipoEquipo;

    @OneToMany(mappedBy = "modeloEquipo")
    @JsonManagedReference
    @JsonIgnore
    private List<Equipo> equipos = new ArrayList<>();

    @OneToMany(mappedBy = "modeloEquipo", cascade = CascadeType.ALL)
    @JsonManagedReference
    @JsonIgnore
    private List<Repuesto> repuestos = new ArrayList<>();

    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UnidadMedida unidadMedida=UnidadMedida.HT;

    @OneToMany(mappedBy = "modeloEquipo", cascade = CascadeType.ALL)
    @JsonManagedReference
    @JsonIgnore
    private List<DocumentoModeloEquipo> documentos = new ArrayList<>();

    @Column(nullable = false)
    private int frecuenciaUnidadMedida;

    @Column(nullable = false)
    private int frecuenciaTiempo;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "service_modelo_id", referencedColumnName = "id")
    private ServiceModelo serviceModelo;

    @Transient
    private Long idServiceModelo;

    public void validar() throws SigemaException{
        if(anio < 1900){
            throw new SigemaException("El año debe ser mayor o igual a 1900");
        }

        if(anio > (LocalDate.now().getYear() + 1)){
            throw new SigemaException("El año debe ser menor a " + (LocalDate.now().getYear() + 1));
        }

        if(modelo==null||modelo.isEmpty()){
            throw new SigemaException("Debe ingresar un modelo");
        }

        if(capacidad <= 0){
            throw new SigemaException("La capacidad debe ser mayor a 0");
        }

        if(idMarca == 0 && marca.getId() == 0){
            throw new SigemaException("Debe ingresar una marca");
        }

        if(idTipoEquipo == 0 && tipoEquipo.getId() == 0){
            throw new SigemaException("Debe ingresar un tipo de equipo");
        }
    }
}