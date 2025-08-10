package com.example.sigema.services.implementations;

import com.example.sigema.models.Unidad;
import com.example.sigema.models.UnidadEmail;
import com.example.sigema.repositories.IUnidadEmailRepository;
import com.example.sigema.repositories.IUnidadRepository;
import com.example.sigema.services.ILogService;
import com.example.sigema.services.IUnidadEmailService;
import com.example.sigema.utilidades.SigemaException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class UnidadEmailService implements IUnidadEmailService {

    @Autowired
    private IUnidadRepository unidadRepository;

    @Autowired
    private IUnidadEmailRepository unidadEmailRepository;

    @Autowired
    private ILogService logService;

    @Override
    public UnidadEmail agregarEmail(Long unidadId, String email) throws Exception {
        Unidad unidad = unidadRepository.findById(unidadId)
                .orElseThrow(() -> new SigemaException("Unidad no encontrada"));

        UnidadEmail unidadEmail = new UnidadEmail();
        unidadEmail.setEmail(email);
        unidadEmail.setUnidad(unidad);

        UnidadEmail ue = unidadEmailRepository.save(unidadEmail);

        logService.guardarLog("Se han asignado el/los email/s "+ ue.getEmail() +" en la unidad " + unidad.getNombre(), true);

        return ue;
    }

    @Override
    public void eliminarEmail(Long emailId) throws Exception {
        UnidadEmail email = unidadEmailRepository.findById(emailId)
                .orElseThrow(() -> new SigemaException("Email no encontrado"));

        Unidad unidad = email.getUnidad();

        List<UnidadEmail> emails = unidadEmailRepository.findByUnidad(unidad);

        if (emails.size() <= 1) {
            throw new SigemaException("No se puede eliminar el último email de la unidad.");
        }

        unidadEmailRepository.deleteById(emailId);
        logService.guardarLog("Se han elimnado el/los email/s "+ email.getEmail() +" en la unidad " + unidad.getNombre(), true);
    }


    @Override
    public List<UnidadEmail> obtenerEmailsPorUnidad(Long unidadId) throws Exception {
        Unidad unidad = unidadRepository.findById(unidadId)
                .orElseThrow(() -> new SigemaException("Unidad no encontrada"));
        return unidadEmailRepository.findByUnidad(unidad);
    }

}
