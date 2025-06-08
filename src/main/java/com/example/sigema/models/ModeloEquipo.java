package com.example.sigema.models;

import com.example.sigema.models.enums.UnidadMedida;
import com.example.sigema.utilidades.SigemaException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;


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
    private List<Equipo> equipos = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "ModeloEquipoRepuesto", joinColumns = @JoinColumn(name = "modeloEquipoId"), inverseJoinColumns = @JoinColumn(name = "repuestoId"))
    private List<Repuesto> repuestos = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UnidadMedida unidadMedida=UnidadMedida.HT;

    @OneToMany(mappedBy = "modeloEquipo", cascade = CascadeType.ALL)//cascade hace que si borro el modelo, borro los docs
    @JsonManagedReference
    private List<DocumentoModeloEquipo> documentos = new ArrayList<>();


    public UnidadMedida getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(UnidadMedida unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public double getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(double capacidad) {
        this.capacidad = capacidad;
    }

    public Marca getMarca() {
        return marca;
    }

    public void setMarca(Marca marca) {
        this.marca = marca;
    }

    public Long getIdTipoEquipo() {
        return idTipoEquipo;
    }

    public void setIdTipoEquipo(Long idTipoEquipo) {
        this.idTipoEquipo = idTipoEquipo;
    }

    public TipoEquipo getTipoEquipo() {
        return tipoEquipo;
    }

    public void setTipoEquipo(TipoEquipo tipoEquipo) {
        this.tipoEquipo = tipoEquipo;
    }

    public List<Repuesto> getRepuestos() {
        return repuestos;
    }

    public void setRepuestos(List<Repuesto> repuestos) {
        this.repuestos = repuestos;
    }

    public List<Equipo> getEquipos() {
        return equipos;
    }

    public void setEquipos(List<Equipo> equipos) {
        this.equipos = equipos;
    }

    public Long getIdMarca() {
        return idMarca;
    }

    public void setIdMarca(Long idMarca) {
        this.idMarca = idMarca;
    }

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

    public List<DocumentoModeloEquipo> getDocumentos() {
        return documentos;
    }

    public void setDocumentos(List<DocumentoModeloEquipo> documentos) {
        this.documentos = documentos;
    }

}