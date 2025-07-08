package com.example.sigema.models;

import com.example.sigema.utilidades.SigemaException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Table(name = "Marcas")
@Getter
@Setter
public class Marca implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;

    public void validar() throws SigemaException{
        if(nombre==null||nombre.isEmpty()){
            throw new SigemaException("Debe ingresar el nombre");
        }
    }
}
