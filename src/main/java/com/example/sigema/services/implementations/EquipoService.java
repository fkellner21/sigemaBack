package com.example.sigema.services.implementations;

import com.example.sigema.models.Equipo;
import com.example.sigema.repositories.IEquipoService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public class EquipoService implements IEquipoService {

    //Esto para manejar la bdd gurices, se controla desde aca el entity
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Equipo> Listar() throws Exception{
        return entityManager.createQuery("SELECT e FROM Equipo e", Equipo.class).getResultList();
    }

    @Override
    public void Agregar(Equipo equipo) throws Exception {
        entityManager.persist(equipo);
    }

    @Override
    public void Eliminar(Long id) throws Exception{
        Equipo equipoAux = entityManager.find(Equipo.class, id);
        if(equipoAux != null) {
            entityManager.remove(equipoAux);
        }
    }

    @Override
    public Equipo Buscar(Long id) throws Exception{
        return entityManager.find(Equipo.class, id);
    }

    @Override
    public void Modificar(Equipo equipo) throws Exception {
        if(equipo != null) {
            if(entityManager.contains(equipo)) {
                entityManager.merge(equipo);
            }
        }
    }
}
