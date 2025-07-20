package com.example.sigema.models;

import com.example.sigema.models.enums.UnidadMedida;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date fechaMantenimiento;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date fechaRegistro;

    @Transient
    private Long idEquipo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipo")
    @JsonBackReference
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