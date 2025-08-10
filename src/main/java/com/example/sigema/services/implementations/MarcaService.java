package com.example.sigema.services.implementations;

import com.example.sigema.models.Marca;
import com.example.sigema.repositories.IMarcaRepository;
import com.example.sigema.services.ILogService;
import com.example.sigema.services.IMarcaService;
import com.example.sigema.utilidades.SigemaException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MarcaService implements IMarcaService {
    private final IMarcaRepository marcaRepository;
    private final ILogService logService;

    public MarcaService(IMarcaRepository marcaRepository, ILogService logService) {
        this.marcaRepository = marcaRepository;
        this.logService = logService;
    }

    @Override
    public Marca Crear(Marca marca) throws Exception {
        marca.validar();

        Marca existente = marcaRepository.findByNombre(marca.getNombre()).orElse(null);

        if(existente != null){
            throw new SigemaException("Ya existe una marca con ese nombre");
        }

        Marca creado = marcaRepository.save(marca);

        logService.guardarLog("Se ha creado la marca " + marca.getNombre(), true);

        return creado;
    }

    @Override
    public Marca Editar(Long id, Marca marca) throws Exception {
        marca.validar();

        Marca existente = marcaRepository.findByNombre(marca.getNombre()).orElse(null);

        if(existente != null && !existente.getId().equals(id)){
            throw new SigemaException("Ya existe una marca con ese nombre");
        }

        Marca marcaExistenteOpt = marcaRepository.findById(id).orElse(null);

        if (marcaExistenteOpt == null) {
            throw new Exception("Marca con ID " + id + " no encontrado");
        }

        marcaExistenteOpt.setNombre(marca.getNombre());

        Marca editado = marcaRepository.save(marcaExistenteOpt);

        logService.guardarLog("Se ha editado la marca " + marca.getNombre(), true);

        return editado;
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