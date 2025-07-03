package com.example.sigema.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "Actuaciones")
@Getter
@Setter
public class Actuacion implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "tramite_id", referencedColumnName = "id")
    @JsonIgnore
    private Tramite tramite;

    public Long getId() {
        return id;
    }

    @ManyToOne
    @JoinColumn(name = "usuario_id", referencedColumnName = "id")
    private Usuario usuario;

    @Lob
    @Column(nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String descripcion;

    @Column
    private Date fecha;
}