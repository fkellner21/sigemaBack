package com.example.sigema.repositories;

import com.example.sigema.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IRepositoryUsuario extends JpaRepository<Usuario, Long> {
}
