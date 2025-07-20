package com.example.sigema.services.implementations;
import com.example.sigema.models.Equipo;
import com.example.sigema.models.Mantenimiento;
import com.example.sigema.repositories.IMantenimientoRepository;
import com.example.sigema.services.IMantenimientoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MantenimientoService implements IMantenimientoService {

    @Autowired
    private IMantenimientoRepository repo;

    @Override
    public List<Mantenimiento> obtenerTodos() throws Exception {
            return repo.findAll();
    }

    @Override
    public Optional<Mantenimiento> obtenerPorId(Long id) {
        return repo.findById(id);
    }

    @Override
    public Mantenimiento crear(Mantenimiento mantenimiento) {
        return repo.save(mantenimiento);
    }

    @Override
    public Mantenimiento editar(Long id, Mantenimiento mantenimientoActualizado) {
        Mantenimiento existente = repo.findById(id).orElse(null);
        if (existente != null) {
            existente.setDescripcion(mantenimientoActualizado.getDescripcion());
            existente.setFechaMantenimiento(mantenimientoActualizado.getFechaMantenimiento());
            existente.setRepuestosMantenimiento(mantenimientoActualizado.getRepuestosMantenimiento());
            return repo.save(existente);
        }
        return null;
    }

    @Override
    public void eliminar(Long id) {
        repo.deleteById(id);
    }

    @Override
    public List<Mantenimiento> obtenerPorEquipo(Long idEquipo) throws Exception {
        return repo.findByEquipo_IdOrderByFechaMantenimientoDesc(idEquipo);
    }

}
