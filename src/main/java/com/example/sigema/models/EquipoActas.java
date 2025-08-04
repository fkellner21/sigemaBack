package com.example.sigema.models;

import lombok.Getter;
import lombok.Setter;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class EquipoActas {
    public Equipo equipo;
    public List<ReporteActa> actas = new ArrayList<>();
}
