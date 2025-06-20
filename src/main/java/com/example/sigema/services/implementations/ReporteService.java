package com.example.sigema.services.implementations;

import com.example.sigema.models.Equipo;
import com.example.sigema.models.Reporte;
import com.example.sigema.models.enums.UnidadMedida;
import com.example.sigema.services.IEquipoService;
import com.example.sigema.services.IReporteService;
import com.example.sigema.utilidades.SigemaException;
import org.springframework.stereotype.Service;

@Service
public class ReporteService implements IReporteService {
    private IEquipoService equipoService;

    public ReporteService(IEquipoService equipoService) {
        this.equipoService = equipoService;
    }


    @Override
    public void newReporte(Reporte reporte) throws SigemaException {
        try{
            reporte.validar();
            Equipo equipo=equipoService.ObtenerPorId(reporte.getIdEquipo());
            equipo.setLatitud(reporte.getLatitud());
            equipo.setLongitud(reporte.getLongitud());
            equipo.setFechaUltimaPosicion(reporte.getFecha());
            if (equipo.getModeloEquipo().getUnidadMedida()== UnidadMedida.HT){
                equipo.setCantidadUnidadMedida(equipo.getCantidadUnidadMedida()+reporte.getHorasDeTrabajo());
            }
            if (equipo.getModeloEquipo().getUnidadMedida()==UnidadMedida.KMs){
                equipo.setCantidadUnidadMedida(equipo.getCantidadUnidadMedida()+reporte.getKilometros());
            }
            equipoService.Editar(equipo.getId(), equipo);
        }catch (Exception e){
            throw new SigemaException(e.getMessage());
        }
    }

}
