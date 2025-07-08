package com.example.sigema.models;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonBackReference;


@Entity
@Table(name = "DocumentosModeloEquipo")
@Getter
@Setter
public class DocumentoModeloEquipo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String nombreArchivo;

    @Column(columnDefinition = "TEXT")
    private String rutaArchivo;

    @ManyToOne(optional = false)
    @JoinColumn(name = "modelo_equipo_id")
    @JsonBackReference
    private ModeloEquipo modeloEquipo;

    private LocalDate fechaSubida = LocalDate.now();
}
