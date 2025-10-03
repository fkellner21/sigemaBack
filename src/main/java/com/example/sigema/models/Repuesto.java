package com.example.sigema.models;

import com.example.sigema.models.enums.TipoRepuesto;
import com.example.sigema.utilidades.SigemaException;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

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

    @ManyToOne(optional = false)
    @JoinColumn(name = "modelo_equipo_id", referencedColumnName = "id")
    @JsonBackReference
    private ModeloEquipo modeloEquipo;

    @Column
    private String codigoSICE;

    @OneToMany(mappedBy = "repuesto", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<RepuestoMantenimiento> repuestosMantenimiento;

    public void validar() throws SigemaException {
        if(idModelo == null){
            throw new SigemaException("Debe ingresar el modelo");
        }

        if(nombre == null || nombre.isEmpty()){
            throw new SigemaException("Debe ingresar el nombre");
        }

        if(cantidad <= 0){
            throw new SigemaException("Debe ingresar la cantidad");
        }

        if(tipo != TipoRepuesto.Pieza && tipo != TipoRepuesto.Lubricante){
            throw new SigemaException("El tipo de repuesto debe ser Pieza o Lubricante");
        }
    }
}
