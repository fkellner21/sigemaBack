package com.example.sigema.models;

import com.example.sigema.models.enums.TipoRepuesto;
import com.example.sigema.utilidades.SigemaException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Table(name = "Repuestos")
@Getter
@Setter
public class Repuesto implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long idModelo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoRepuesto tipo;

    @Column(nullable = false)
    private String nombre;

    @Column
    private String caracteristicas;

    @Column(nullable = false)
    private double cantidad;

    @Column
    private String observaciones;

    public String getCodigoSICE() {
        return codigoSICE;
    }

    public void setCodigoSICE(String codigoSICE) {
        this.codigoSICE = codigoSICE;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public double getCantidad() {
        return cantidad;
    }

    public void setCantidad(double cantidad) {
        this.cantidad = cantidad;
    }

    public String getCaracteristicas() {
        return caracteristicas;
    }

    public void setCaracteristicas(String caracteristicas) {
        this.caracteristicas = caracteristicas;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public TipoRepuesto getTipo() {
        return tipo;
    }

    public void setTipo(TipoRepuesto tipo) {
        this.tipo = tipo;
    }

    public Long getIdModelo() {
        return idModelo;
    }

    public void setIdModelo(Long idModelo) {
        this.idModelo = idModelo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(unique = true)
    private String codigoSICE;

    public void validar() throws SigemaException {
        if(idModelo == null){
            throw new SigemaException("Debe ingresar el modelo");
        }

        if(nombre == null || nombre.isEmpty()){
            throw new SigemaException("Debe ingresar el nombre");
        }

        if(codigoSICE == null || codigoSICE.isEmpty()){
            throw new SigemaException("Debe ingresar el código SICE");
        }

        if(cantidad <= 0){
            throw new SigemaException("Debe ingresar la cantidad");
        }

        if(tipo != TipoRepuesto.Pieza && tipo != TipoRepuesto.Lubricante){
            throw new SigemaException("El tipo de repuesto debe ser Pieza o Lubricante");
        }
    }
}
