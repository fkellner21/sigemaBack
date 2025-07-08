package com.example.sigema.models;

import com.example.sigema.models.enums.Rol;
import com.example.sigema.utilidades.SigemaException;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

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

    @Column(nullable = true)
    @JsonBackReference
    private String password;

    @ManyToOne
    @JoinColumn(name = "grado_id", nullable = true)
    private Grado grado;

    @ManyToOne
    @JoinColumn(name = "unidad_id", nullable = true)
    private Unidad unidad;

    @Transient
    private Long idGrado;

    @Transient
    private Long idUnidad;

    @Column(nullable = true)
    private Long telefono;

    @Column(nullable = false, unique = true)
    private String cedula;

    @Column(nullable = false)
    private boolean activo = false;

    public Usuario(String cedula, String password, Rol rol) {
        this.cedula = cedula;
        this.password = password;
        this.rol = rol;
    }

    public Usuario() {

    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol;


    public void validar() throws SigemaException {
        if (!verificarFormatoCI(this.cedula)) throw new SigemaException("Cedula inválida");
    }

    private boolean verificarFormatoCI(String input) {
        // Elimina puntos, barras y guiones
        String limpio = input.replaceAll("[./-]", "");

        // Verifica si quedan exactamente 8 dígitos
        return limpio.matches("\\d{8}");
    }
}