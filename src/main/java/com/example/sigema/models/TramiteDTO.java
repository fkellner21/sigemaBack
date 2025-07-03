package com.example.sigema.models;

import com.example.sigema.models.enums.TipoTramite;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TramiteDTO {

    private TipoTramite tipoTramite;

    private Long idUnidadOrigen;

    private Long idUnidadDestino;

    private Long idEquipo;

    private Long idRepuesto;

    private String texto;
}