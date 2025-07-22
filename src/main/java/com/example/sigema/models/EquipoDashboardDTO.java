package com.example.sigema.models;

import com.example.sigema.models.enums.EstadoEquipo;
import com.example.sigema.models.enums.TareaEquipo;
import com.example.sigema.models.enums.UnidadMedida;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class EquipoDashboardDTO {
    private String matricula;
    private String unidad;
    private int anio;
    private String observaciones;
    private double cantidadUnidadMedida;
    private UnidadMedida unidadMedida;
    private double capacidad;
    private String marca;
    private String tipoEquipo;
    private TareaEquipo tareaEquipo;
    private double latitud;
    private double longitud;
    private Date fechaUltimaPosicion;
    private EstadoEquipo estado;

    public EquipoDashboardDTO fromEquipo(Equipo equipo) {
        this.matricula = equipo.getMatricula();
        this.unidad=equipo.getUnidad().getNombre();
        this.anio= equipo.getModeloEquipo().getAnio();
        this.observaciones = equipo.getObservaciones();
        this.cantidadUnidadMedida = equipo.getCantidadUnidadMedida();
        this.unidadMedida=equipo.getModeloEquipo().getUnidadMedida();
        this.capacidad=equipo.getModeloEquipo().getCapacidad();
        this.marca=equipo.getModeloEquipo().getMarca().getNombre();
        this.tipoEquipo=equipo.getModeloEquipo().getTipoEquipo().getCodigo();
        this.tareaEquipo=equipo.getModeloEquipo().getTipoEquipo().getTarea();
        this.latitud=equipo.getLatitud();
        this.longitud=equipo.getLongitud();
        this.fechaUltimaPosicion=equipo.getFechaUltimaPosicion();
        this.estado=equipo.getEstado();
        return this;
    }

}
