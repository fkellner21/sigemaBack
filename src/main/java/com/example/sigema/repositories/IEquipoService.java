package com.example.sigema.repositories;

import com.example.sigema.models.Equipo;

import java.util.List;

public interface IEquipoService {

    public List<Equipo> Listar() throws Exception;
    public void Agregar(Equipo equipo) throws Exception;
    public void Eliminar(Long id) throws Exception;
    public Equipo Buscar(Long id) throws Exception;
    public void Modificar(Equipo equipo) throws Exception;

}
