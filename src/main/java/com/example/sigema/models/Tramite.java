package com.example.sigema.models;

import com.example.sigema.models.enums.EstadoTramite;
import com.example.sigema.models.enums.Rol;
import com.example.sigema.models.enums.TipoTramite;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "Tramites")
@Getter
@Setter
public class Tramite implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoTramite tipoTramite;

    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoTramite estado = EstadoTramite.Iniciado;

    @Column(nullable = false)
    private Date fechaInicio;

    @Column
    private String texto;

    @ManyToOne(optional = false)
    @JoinColumn(name = "unidad_origen_id", referencedColumnName = "id")
    private Unidad unidadOrigen;

    @ManyToOne
    @JoinColumn(name = "unidad_destino_id", referencedColumnName = "id")
    private Unidad unidadDestino;

    @Column
    private Long idGradoUsuarioSolicitado;

    @Column
    private String nombreCompletoUsuarioSolicitado;

    @Column
    private String cedulaUsuarioSolicitado;

    @Column
    private Long telefonoUsuarioSolicitado;

    @Column
    private Long idUsuarioBajaSolicitada;

    @Column
    private Rol rolSolicitado;

    @Column
    private Long idUnidadUsuarioSolicitado;

    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id", referencedColumnName = "id")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "repuesto_id", referencedColumnName = "id")
    private Repuesto repuesto;


    @OneToMany(mappedBy = "tramite", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private final List<Actuacion> actuaciones = new ArrayList<>();

    @OneToMany(mappedBy = "tramite", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private final List<VisualizacionTramite> visualizaciones = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "equipo_id", referencedColumnName = "id")
    private Equipo equipo;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tramite tramite = (Tramite) o;
        return id != null && id.equals(tramite.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public void actualizarEstado(Usuario quienAbre){
        //siempre que alguien de la unidad destino abra un tramite iniciado
        if(estado == EstadoTramite.Iniciado && unidadDestino==null){
            if(!unidadOrigen.equals(quienAbre.getUnidad())) estado=EstadoTramite.EnTramite;
        } else if(unidadDestino!=null && estado == EstadoTramite.Iniciado && unidadDestino.equals(quienAbre.getUnidad()) && !Objects.equals(usuario.getId(), quienAbre.getId())){
            estado=EstadoTramite.EnTramite;
        }

    }
}