package com.example.sigema.repositories;

import com.example.sigema.models.RepuestoMantenimiento;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface IRepuestoMantenimientoRepository extends JpaRepository<RepuestoMantenimiento, Long> {

    @Modifying
    @Transactional
    @Query(
            value = "DELETE FROM Repuestos_Mantenimientos WHERE mantenimiento = :idMantenimiento",
            nativeQuery = true
    )
    void borrarPorMantenimiento(@Param("idMantenimiento") Long idMantenimiento);
}
