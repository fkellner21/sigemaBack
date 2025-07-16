package com.example.sigema.repositories;

import com.example.sigema.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IRepositoryUsuario extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByCedula(String cedula);
    List<Usuario> findAllByUnidad_Id(Long idUnidad);
    List<Usuario> findAllByActivoTrue();
}
