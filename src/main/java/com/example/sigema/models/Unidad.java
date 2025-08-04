package com.example.sigema.models;

import com.example.sigema.utilidades.SigemaException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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

    @Column(nullable = false)
    private boolean esGranUnidad;

    @OneToMany(mappedBy = "unidad", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UnidadEmail> emails = new ArrayList<>();


    public void validar() throws SigemaException {
        if(nombre.isEmpty()){
            throw new SigemaException("Debes ingresar un nombre");
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Unidad otra = (Unidad) obj;
        return id != null && id.equals(otra.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

}