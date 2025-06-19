package com.example.sigema.models;

import com.example.sigema.utilidades.SigemaException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "Reportes")
@Getter
@Setter
public class Reporte implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long idEquipo;

    @Column(nullable = false)
    private double latitud;

    @Column(nullable = false)
    private double longitud;

    @Column(nullable = false)
    private Date fecha;

    @Column(nullable = false)
    private double horasDeTrabajo;

    @Column(nullable = false)
    private double kilometros;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdEquipo() {
        return idEquipo;
    }

    public void setIdEquipo(Long idEquipo) {
        this.idEquipo = idEquipo;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public double getHorasDeTrabajo() {
        return horasDeTrabajo;
    }

    public void setHorasDeTrabajo(double horasDeTrabajo) {
        this.horasDeTrabajo = horasDeTrabajo;
    }

    public double getKilometros() {
        return kilometros;
    }

    public void setKilometros(double kilometros) {
        this.kilometros = kilometros;
    }
    public void validar() throws SigemaException {
        if(this.fecha==null||this.fecha.after(new Date())){
            throw new SigemaException("La fecha del reporte llega incorrecta");
        }
        if (this.latitud==0 || this.longitud==0){
            throw new SigemaException("La posicion del reporte no es valida");
        }
        if (this.idEquipo==0){
            throw new SigemaException("El idEquipo no es valido");
        }
    }
}


