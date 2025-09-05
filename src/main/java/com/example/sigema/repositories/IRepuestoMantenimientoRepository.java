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
    @Query("DELETE FROM RepuestoMantenimiento r WHERE r.mantenimiento.id = :idMantenimiento")
    void borrarPorMantenimiento(@Param("idMantenimiento") Long idMantenimiento);
}
