package com.example.sigema.models;

import com.example.sigema.models.enums.Rol;
import com.example.sigema.models.enums.TipoTramite;
import jakarta.persistence.Column;
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

    private Long idUsuarioBaja;

    private Long idGradoUsuarioSolicitado;

    private Long idUnidadUsuarioSolicitado;

    private String nombreCompletoUsuarioSolicitado;

    private String cedulaUsuarioSolicitado;

    private Long telefonoUsuarioSolicitado;

    private String texto;

    private Rol rolSolicitado;
}