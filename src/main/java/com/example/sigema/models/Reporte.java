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


