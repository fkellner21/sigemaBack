package com.example.sigema.models;

import com.example.sigema.models.enums.UnidadMedida;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class MantenimientoDTO {
    private String descripcion;
    private String fechaMantenimiento;
    private String fechaRegistro;
    private UnidadMedida unidadMedida;
    private double cantidadUnidadMedida;
    private boolean esService;
    private Long idEquipo;
    private List<RepuestoMantenimiento> repuestosMantenimiento = new ArrayList<>();
}

