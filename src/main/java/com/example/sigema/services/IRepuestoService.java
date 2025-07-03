package com.example.sigema.services;

import com.example.sigema.models.Marca;
import com.example.sigema.models.Repuesto;
import com.example.sigema.models.enums.TipoRepuesto;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;

public interface IRepuestoService {
    List<Repuesto> ObtenerTodos(Long idModelo, TipoRepuesto tipoRepuesto) throws Exception;

    Repuesto Crear(Repuesto r) throws Exception;

    Optional<Repuesto> ObtenerPorId(Long id) throws Exception;

    Repuesto Editar(Long id, Repuesto r) throws Exception;
}
