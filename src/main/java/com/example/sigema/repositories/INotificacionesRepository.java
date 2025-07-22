package com.example.sigema.repositories;

import com.example.sigema.models.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface INotificacionesRepository extends JpaRepository<Notificacion, Long> {
    Optional<List<Notificacion>> findAllByIdUsuario(Long idUsuario);
    Optional<List<Notificacion>> findByIdUsuarioAndIdTramite(Long idUsuario, Long idTramite);
}