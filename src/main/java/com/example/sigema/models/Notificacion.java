package com.example.sigema.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "Notificaciones")
@Getter
@Setter
public class Notificacion implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long idTramite;

    @Column(nullable = false)
    private Long idUsuario;

    @Column(nullable = false)
    private String texto;

    @Column(nullable = false)
    private Date fecha;
}