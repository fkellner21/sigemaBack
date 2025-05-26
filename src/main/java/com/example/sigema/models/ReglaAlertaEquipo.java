package com.example.sigema.models;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

//@Entity
//@Table(name = "ReglaAlertaEquipo")
@Getter
@Setter
public class ReglaAlertaEquipo implements Serializable {

    @Column(nullable = false)
    private Long idReglaAlerta;

    @Column(nullable = false)
    private Long idEquipo;
}