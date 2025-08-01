package com.example.sigema.controllers;

import com.example.sigema.models.Reporte;
import com.example.sigema.services.IReporteService;
import com.example.sigema.utilidades.SigemaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reporte")
//@CrossOrigin(origins = "*")
public class ReporteController {

    private final IReporteService reporteService;

    @Autowired
    public ReporteController(IReporteService reporteService) {
        this.reporteService = reporteService;
    }

    @PostMapping
    public ResponseEntity<?> newReporte(@RequestBody Reporte reporte) {
        try {
            reporteService.newReporte(reporte);

            return ResponseEntity.ok().build();
        } catch(SigemaException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().body("Ha ocurrido un error, vuelva a intentarlo");
        }
    }
}
