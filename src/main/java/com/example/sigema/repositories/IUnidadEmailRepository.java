package com.example.sigema.repositories;

import com.example.sigema.models.Unidad;
import com.example.sigema.models.UnidadEmail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IUnidadEmailRepository extends JpaRepository<UnidadEmail, Long> {
    List<UnidadEmail> findByUnidad(Unidad unidad);
}
