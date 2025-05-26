package com.example.sigema.repositories;

import com.example.sigema.models.Marca;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IMarcaRepository extends JpaRepository<Marca, Long> {
}
