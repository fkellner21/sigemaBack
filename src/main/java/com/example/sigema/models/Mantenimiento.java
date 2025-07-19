package com.example.sigema.models;

import com.example.sigema.models.enums.UnidadMedida;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "Mantenimientos")
@Getter
@Setter
public class Mantenimiento implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String descripcion;

    @Column(nullable = false)
    private Date fechaMantenimiento;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    private Date fechaRegistro;

    @Column(nullable = false, insertable = false, updatable = false)
    private Long idEquipo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idEquipo", nullable = false)
    private Equipo equipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UnidadMedida unidadMedida;

    @Column(nullable = false)
    private double cantidadUnidadMedida;


    @OneToMany(mappedBy = "mantenimiento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RepuestoMantenimiento> repuestosMantenimiento;

    @Column(nullable = false)
    private boolean esService;
}
