package com.example.sigema.services.implementations;

import com.example.sigema.models.Marca;
import com.example.sigema.repositories.IMarcaRepository;
import com.example.sigema.services.IMarcaService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service // mejor que @Repository para servicios
@Transactional
public class MarcaService implements IMarcaService {
    private final IMarcaRepository marcaRepository;

    public MarcaService(IMarcaRepository marcaRepository) {
        this.marcaRepository = marcaRepository;
    }

    @Override
    public Marca Crear(Marca marca) throws Exception {
        return marcaRepository.save(marca);
    }

    @Override
    public Marca Editar(Long id, Marca marca) throws Exception {
        Marca marcaExistenteOpt = marcaRepository.findById(id).orElse(null);

        if (marcaExistenteOpt == null) {
            throw new Exception("Marca con ID " + id + " no encontrado");
        }

        marcaExistenteOpt.setNombre(marca.getNombre());

        return marcaRepository.save(marcaExistenteOpt);
    }

    @Override
    public Optional<Marca> ObtenerPorId(Long id) throws Exception {
        return marcaRepository.findById(id);
    }

    @Override
    public List<Marca> ObtenerTodos() {
        return marcaRepository.findAll();
    }
}
