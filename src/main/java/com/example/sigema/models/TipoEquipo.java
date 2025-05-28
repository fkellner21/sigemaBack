package com.example.sigema.models;

import com.example.sigema.utilidades.SigemaException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Table(name = "TiposEquipos")
@Getter
@Setter
public class TipoEquipo implements Serializable {
    public void setId(Long id) {
        this.id = id;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String codigo;

    @Column(nullable = true, unique = true)
    private String nombre;

    @Column(nullable = false)
    private boolean activo = false;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public Long getId() {
        return id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public void validar() throws SigemaException{
        if(nombre.isEmpty()){
            throw new SigemaException("Debe ingresar un nombre");
        }

        if(codigo.isEmpty()){
            throw new SigemaException("Debe ingresar un código");
        }
    }
}