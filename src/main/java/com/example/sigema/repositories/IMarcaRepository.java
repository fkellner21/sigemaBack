package com.example.sigema.repositories;

import com.example.sigema.models.Marca;
import com.example.sigema.models.TipoEquipo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IMarcaRepository extends JpaRepository<Marca, Long> {
    Optional<Marca> findByNombre(String nombre);
}
