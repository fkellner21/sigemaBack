package com.example.sigema.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.io.Serializable;

@Entity
@Table(name = "ReglaAlertaEquipo")
public class ReglaAlertaEquipo implements Serializable {

    @Column(nullable = false)
    private Long idReglaAlerta;

    @Column(nullable = false)
    private Long idEquipo;
}