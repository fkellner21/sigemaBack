package com.example.sigema.services.implementations;

import com.example.sigema.models.Notificacion;
import com.example.sigema.repositories.INotificacionesRepository;
import com.example.sigema.services.INotificacionesService;
import com.example.sigema.utilidades.SigemaException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class NotificacionService implements INotificacionesService {

    private final INotificacionesRepository notificacionesRepository;

    public NotificacionService(INotificacionesRepository notificacionesRepository){
        this.notificacionesRepository = notificacionesRepository;
    }

    @Override
    public void Eliminar(Long id) throws Exception {
        Notificacion notificacion = ObtenerPorId(id);

        notificacionesRepository.delete(notificacion);
    }

    @Override
    public Notificacion ObtenerPorId(Long id) throws Exception {
        Notificacion notificacion = notificacionesRepository.findById(id).orElse(null);

        if(notificacion == null){
            throw new SigemaException("No se encontro la notificación");
        }

        return notificacion;
    }

    @Override
    public List<Notificacion> obtenerPorIdUsuario(Long idUsuario) throws Exception {
        return notificacionesRepository.findAllByIdUsuario(idUsuario).orElse(new ArrayList<>());
    }

    @Override
    public List<Notificacion> obtenerPorIdUsuarioAndIdTramite(Long idUsuario, Long idTramite) throws Exception {
        return notificacionesRepository.findByIdUsuarioAndIdTramite(idUsuario, idTramite).orElse(new ArrayList<>());
    }

    @Override
    public void Crear(Notificacion notificacion) throws Exception {
        notificacionesRepository.save(notificacion);
    }
}