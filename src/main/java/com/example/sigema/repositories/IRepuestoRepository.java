package com.example.sigema.repositories;

import com.example.sigema.models.Equipo;
import com.example.sigema.models.Repuesto;
import com.example.sigema.models.enums.TipoRepuesto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IRepuestoRepository extends JpaRepository<Repuesto, Long> {
    Optional<Repuesto> findByNombre(String nombre);
    List<Repuesto> findByIdModeloAndTipo(Long modeloId, TipoRepuesto tipoRepuesto);
    Optional<Repuesto> findByCodigoSICE(String codigoSICE);
}