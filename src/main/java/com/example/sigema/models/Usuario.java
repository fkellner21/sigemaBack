package com.example.sigema.models;

import com.example.sigema.models.enums.Rol;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Table(name = "Usuarios")
@Getter
@Setter
public class Usuario implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombreCompleto;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private Long idGrado;

    @Column(nullable = false)
    private Long idUnidad;

    @Column(nullable = false)
    private Long telefono;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol;


    public void validar() {
    }
}