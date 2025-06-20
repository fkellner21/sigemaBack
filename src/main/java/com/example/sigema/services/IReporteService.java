package com.example.sigema.services;

import com.example.sigema.models.Reporte;
import com.example.sigema.utilidades.SigemaException;

public interface IReporteService {
    public void newReporte(Reporte reporte) throws SigemaException;

}
