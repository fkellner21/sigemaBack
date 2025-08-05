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
    private final IEquipoService equipoService;

    public ReporteService(IEquipoService equipoService) {
        this.equipoService = equipoService;
    }


    @Override
    public void newReporte(Reporte reporte) throws SigemaException {
        try {
            reporte.validar();

            Equipo equipo = equipoService.ObtenerPorId(reporte.getIdEquipo());
            equipo.setLatitud(reporte.getLatitud());
            equipo.setLongitud(reporte.getLongitud());
            equipo.setFechaUltimaPosicion(reporte.getFecha());

            UnidadMedida unidad = equipo.getModeloEquipo().getUnidadMedida();

            double nuevaCantidad = equipo.getCantidadUnidadMedida(); // lo que ya tenía

            if (unidad == UnidadMedida.HT) {
                nuevaCantidad += reporte.getHorasDeTrabajo();
            } else if (unidad == UnidadMedida.KMs) {
                nuevaCantidad += reporte.getKilometros();
            }

            equipo.setCantidadUnidadMedida(nuevaCantidad);

            equipoService.Editar(equipo.getId(), equipo);

        } catch (Exception e) {
            throw new SigemaException(e.getMessage());
        }
    }

}
