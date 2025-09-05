package com.example.sigema.models;

import com.example.sigema.models.enums.UnidadMedida;
import com.example.sigema.utilidades.SigemaException;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.Instant;
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
    private Date fechaRegistro = Date.from(Instant.now());

    @Transient
    private Long idEquipo;

    @ManyToOne
    @JoinColumn(name = "equipo")
    private Equipo equipo;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UnidadMedida unidadMedida;

    @Column(nullable = false)
    private double cantidadUnidadMedida = 0;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "mantenimiento")
    private List<RepuestoMantenimiento> repuestosMantenimiento;

    @Column(nullable = false)
    private boolean esService;

    public void validar() {
        if (equipo==null)   throw new SigemaException("Equipo no encontrado");
        if (cantidadUnidadMedida>equipo.getCantidadUnidadMedida()) throw new SigemaException("HT/Km debe ser igual o inferior al actual");
        if (fechaMantenimiento.after(fechaRegistro)) throw new SigemaException("No se puede registrar mantenimientos a futuro");
    }
}