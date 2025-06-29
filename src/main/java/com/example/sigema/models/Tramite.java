package com.example.sigema.models;

import com.example.sigema.models.enums.EstadoTramite;
import com.example.sigema.models.enums.TipoTramite;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "Tramites")
@Getter
@Setter
public class Tramite implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoTramite tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoTramite estado = EstadoTramite.EnTramite;

    @Column(nullable = false)
    private Date fechaInicio;

    @ManyToOne(optional = false)
    @JoinColumn(name = "unidad_origen_id", referencedColumnName = "id")
    private Unidad unidadOrigen;

    @ManyToOne(optional = false)
    @JoinColumn(name = "unidad_destino_id", referencedColumnName = "id")
    private Unidad unidadDestino;

    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id", referencedColumnName = "id")
    private Usuario usuario;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoTramite getTipo() {
        return tipo;
    }

    public void setTipo(TipoTramite tipo) {
        this.tipo = tipo;
    }

    public EstadoTramite getEstado() {
        return estado;
    }

    public void setEstado(EstadoTramite estado) {
        this.estado = estado;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Unidad getUnidadOrigen() {
        return unidadOrigen;
    }

    public void setUnidadOrigen(Unidad unidadOrigen) {
        this.unidadOrigen = unidadOrigen;
    }

    public Unidad getUnidadDestino() {
        return unidadDestino;
    }

    public void setUnidadDestino(Unidad unidadDestino) {
        this.unidadDestino = unidadDestino;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Repuesto getRepuesto() {
        return repuesto;
    }

    public void setRepuesto(Repuesto repuesto) {
        this.repuesto = repuesto;
    }

    public List<EstadosHistoricoTramite> getHistorico() {
        return historico;
    }

    public void setHistorico(List<EstadosHistoricoTramite> historico) {
        this.historico = historico;
    }

    @ManyToOne
    @JoinColumn(name = "equipo_id", referencedColumnName = "id")
    private Equipo equipo;

    public Equipo getEquipo() {
        return equipo;
    }

    public void setEquipo(Equipo equipo) {
        this.equipo = equipo;
    }

    @ManyToOne
    @JoinColumn(name = "repuesto_id", referencedColumnName = "id")
    private Repuesto repuesto;

    @OneToMany(mappedBy = "tramite", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EstadosHistoricoTramite> historico = new ArrayList<>();

    @OneToMany(mappedBy = "actuacion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Actuacion> actuaciones = new ArrayList<>();

    public List<Actuacion> getActuaciones() {
        return actuaciones;
    }

    public void setActuaciones(List<Actuacion> actuaciones) {
        this.actuaciones = actuaciones;
    }
}