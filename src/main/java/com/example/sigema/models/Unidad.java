package com.example.sigema.models;

import com.example.sigema.utilidades.SigemaException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Table(name = "Unidades")
@Getter
@Setter
public class Unidad implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;

    @Column(nullable = false)
    private float latitud;

    @Column(nullable = false)
    private float longitud;

    public void validar() throws SigemaException {
        if(nombre.isEmpty()){
            throw new SigemaException("Debes ingresar un nombre");
        }
    }
}