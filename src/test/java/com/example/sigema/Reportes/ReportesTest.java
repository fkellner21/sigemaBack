package com.example.sigema.Reportes;

import com.example.sigema.models.Reporte;
import com.example.sigema.utilidades.SigemaException;
import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class ReportesTest {

    @Test
    void validar_ReporteValido_NoLanzaExcepcion() {
        Reporte reporte = new Reporte();
        reporte.setIdEquipo(1L);
        reporte.setLatitud(34.56);
        reporte.setLongitud(-56.78);
        reporte.setFecha(new Date()); // hoy

        assertDoesNotThrow(reporte::validar);
    }

    @Test
    void validar_FechaNula_LanzaExcepcion() {
        Reporte reporte = new Reporte();
        reporte.setIdEquipo(1L);
        reporte.setLatitud(34.56);
        reporte.setLongitud(-56.78);
        reporte.setFecha(null);

        SigemaException ex = assertThrows(SigemaException.class, reporte::validar);
        assertEquals("La fecha del reporte llega incorrecta", ex.getMessage());
    }

    @Test
    void validar_FechaFutura_LanzaExcepcion() {
        Reporte reporte = new Reporte();
        reporte.setIdEquipo(1L);
        reporte.setLatitud(34.56);
        reporte.setLongitud(-56.78);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1); // mañana
        reporte.setFecha(calendar.getTime());

        SigemaException ex = assertThrows(SigemaException.class, reporte::validar);
        assertEquals("La fecha del reporte llega incorrecta", ex.getMessage());
    }

    @Test
    void validar_LatitudCero_LanzaExcepcion() {
        Reporte reporte = new Reporte();
        reporte.setIdEquipo(1L);
        reporte.setLatitud(0);
        reporte.setLongitud(-56.78);
        reporte.setFecha(new Date());

        SigemaException ex = assertThrows(SigemaException.class, reporte::validar);
        assertEquals("La posicion del reporte no es valida", ex.getMessage());
    }

    @Test
    void validar_LongitudCero_LanzaExcepcion() {
        Reporte reporte = new Reporte();
        reporte.setIdEquipo(1L);
        reporte.setLatitud(34.56);
        reporte.setLongitud(0);
        reporte.setFecha(new Date());

        SigemaException ex = assertThrows(SigemaException.class, reporte::validar);
        assertEquals("La posicion del reporte no es valida", ex.getMessage());
    }

    @Test
    void validar_IdEquipoCero_LanzaExcepcion() {
        Reporte reporte = new Reporte();
        reporte.setIdEquipo(0L);
        reporte.setLatitud(34.56);
        reporte.setLongitud(-56.78);
        reporte.setFecha(new Date());

        SigemaException ex = assertThrows(SigemaException.class, reporte::validar);
        assertEquals("El idEquipo no es valido", ex.getMessage());
    }
}