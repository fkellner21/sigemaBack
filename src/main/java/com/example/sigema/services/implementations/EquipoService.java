package com.example.sigema.services.implementations;

import com.example.sigema.models.Equipo;
import com.example.sigema.repositories.IEquipoRepository;
import com.example.sigema.services.IEquipoService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service // mejor que @Repository para servicios
@Transactional
public class EquipoService implements IEquipoService {

    private final IEquipoRepository equipoRepository;

    @Autowired
    public EquipoService(IEquipoRepository equipoRepository) {
        this.equipoRepository = equipoRepository;
    }

    @Override
    public List<Equipo> obtenerTodos() throws Exception {
        return equipoRepository.findAll();
    }

    @Override
    public void Crear(Equipo equipo) throws Exception {
        equipoRepository.save(equipo);
    }

    @Override
    public void Eliminar(Long id) throws Exception {
        equipoRepository.deleteById(id);
    }

    @Override
    public Optional<Equipo> obtenerPorId(Long id) throws Exception {
        return equipoRepository.findById(id);
    }

    @Override
    public void Editar(Equipo equipo) throws Exception {
        equipoRepository.save(equipo); // save sirve para crear o actualizar
    }
}

