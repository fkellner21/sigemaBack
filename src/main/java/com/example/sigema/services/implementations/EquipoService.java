package com.example.sigema.services.implementations;

import com.example.sigema.models.*;
import com.example.sigema.repositories.IEquipoRepository;
import com.example.sigema.services.IEquipoService;
import com.example.sigema.services.IMantenimientoService;
import com.example.sigema.services.IModeloEquipoService;
import com.example.sigema.services.IUnidadService;
import com.example.sigema.utilidades.SigemaException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class EquipoService implements IEquipoService {

    private final IEquipoRepository equipoRepository;
    private final IModeloEquipoService modeloEquipoService;
    private final IUnidadService unidadService;


    @Autowired
    private EmailService emailService;

    @Autowired
    @Lazy
    private IMantenimientoService mantenimientoService;

    @Autowired
    public EquipoService(IEquipoRepository equipoRepository, IModeloEquipoService modeloEquipoService, IUnidadService unidadService) {
        this.equipoRepository = equipoRepository;
        this.modeloEquipoService = modeloEquipoService;
        this.unidadService = unidadService;
    }

    @Override
    public List<Equipo> obtenerTodos(Long idUnidad) throws Exception {
        if(idUnidad == null || idUnidad == 0){
            return equipoRepository.findByActivoTrue();
        }else{
            return equipoRepository.findByUnidad_IdAndActivoTrue(idUnidad);
        }
    }

    @Override
    public Equipo Crear(Equipo equipo) throws Exception {
        equipo.validar();
        equipo.setActivo(true);

        Equipo equipoExistente = equipoRepository.findByMatricula(equipo.getMatricula().toUpperCase());
        if(equipoExistente != null){
            throw new SigemaException("Ya existe un equipo con esa matrícula");
        }

        ModeloEquipo modeloEquipo = modeloEquipoService.ObtenerPorId(equipo.getIdModeloEquipo()).orElse(null);

        if(modeloEquipo == null){
            throw new SigemaException("El modelo de equipo ingresado no existe");
        }

        Long idUnidad = equipo.getIdUnidad();

        if(equipo.getUnidad() != null && equipo.getUnidad().getId() != 0){
            idUnidad = equipo.getUnidad().getId();
        }

        Unidad unidad = unidadService.ObtenerPorId(idUnidad).orElse(null);

        if(unidad == null){
            throw new SigemaException("La unidad ingresada no existe");
        }

        equipo.setModeloEquipo(modeloEquipo);
        equipo.setUnidad(unidad);
        equipo.setMatricula(equipo.getMatricula().toUpperCase());
        equipo.setLatitud(unidad.getLatitud());
        equipo.setLongitud(unidad.getLongitud());
        equipo.setFechaUltimaPosicion(new Date());

        return equipoRepository.save(equipo);
    }

    @Override
    public void Eliminar(Long id) throws Exception {
        Equipo equipo = ObtenerPorId(id);
        equipo.setActivo(false);
        Editar(id, equipo);
    }

    @Override
    public Equipo ObtenerPorId(Long id) throws Exception {
        return equipoRepository.findById(id).orElse(null);
    }

    @Override
    public Equipo Editar(Long id, Equipo equipo) throws Exception {
        equipo.validar();

        Long idModelo = equipo.getIdModeloEquipo();

        if (idModelo == null || idModelo == 0) {
            ModeloEquipo me = equipo.getModeloEquipo();
            idModelo = (me != null) ? me.getId() : null;
        }

        if (idModelo == null || idModelo == 0) {
            throw new SigemaException("Debe asociar un modelo válido al equipo");
        }

        ModeloEquipo modeloEquipo = modeloEquipoService.ObtenerPorId(idModelo)
                .orElseThrow(() -> new SigemaException("Modelo de equipo no encontrado"));

        Equipo equipoEditar = ObtenerPorId(id);

        if(modeloEquipo == null){
            throw new SigemaException("El modelo de equipo ingresado no existe");
        }

        if (equipoEditar == null) {
            throw new SigemaException("El equipo no existe");
        }

        Long idUnidad = equipo.getIdUnidad();

        if(equipo.getUnidad() != null && equipo.getUnidad().getId() != 0 && (equipo.getIdUnidad() == null || equipo.getIdUnidad() == 0)){
            idUnidad = equipo.getUnidad().getId();
        }

        Unidad unidad = unidadService.ObtenerPorId(idUnidad).orElse(null);

        equipoEditar.setEstado(equipo.getEstado());
        equipoEditar.setCantidadUnidadMedida(equipo.getCantidadUnidadMedida());
        equipoEditar.setMatricula(equipo.getMatricula().toUpperCase());
        equipoEditar.setUnidad(unidad);
        equipoEditar.setIdModeloEquipo(idModelo);
        equipoEditar.setObservaciones(equipo.getObservaciones());
        equipoEditar.setActivo(equipo.isActivo());


        Equipo equipoGuardado = equipoRepository.save(equipoEditar);

        verificarFrecuenciaYEnviarAlerta(equipoGuardado, modeloEquipo);

        return equipoGuardado;
    }

    String htmlPreventiva = """
<!DOCTYPE html>
<html>
<head>
    <style>
        body { font-family: Arial, sans-serif; background-color: #fffaf0; padding: 20px; }
        .container { background-color: #ffffff; border-radius: 10px; padding: 20px; box-shadow: 0 2px 8px rgba(0,0,0,0.1); border-left: 6px solid #f39c12; }
        .header { font-size: 22px; color: #f39c12; font-weight: bold; margin-bottom: 10px; }
        .content { font-size: 16px; color: #333; }
        .footer { margin-top: 20px; font-size: 12px; color: #999; }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">🟠 ALERTA PREVENTIVA DE MANTENIMIENTO</div>
        <div class="content">
            El equipo <strong>%s</strong> ha superado el <strong>80%%</strong> de su frecuencia de mantenimiento.<br>
            <br>
            Modelo: <strong>%s</strong><br>
            Frecuencia por uso establecida: <strong>%d %s</strong><br>
            Cantidad actual: <strong>%.2f %s</strong><br>
            Frecuencia por tiempo establecida: <strong>%d meses</strong><br>
            Tiempo desde el último service: <strong>%.2f meses</strong>
        </div>
        <div class="footer">
            Este correo fue generado automáticamente por el sistema de mantenimiento.
        </div>
    </div>
</body>
</html>
""";


    String htmlCritica = """
<!DOCTYPE html>
<html>
<head>
    <style>
        body { font-family: Arial, sans-serif; background-color: #fff3f3; padding: 20px; }
        .container { background-color: #ffffff; border-radius: 10px; padding: 20px; box-shadow: 0 2px 8px rgba(0,0,0,0.1); border-left: 6px solid #e74c3c; }
        .header { font-size: 22px; color: #e74c3c; font-weight: bold; margin-bottom: 10px; }
        .content { font-size: 16px; color: #333; }
        .footer { margin-top: 20px; font-size: 12px; color: #999; }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">🔴 ALERTA DE MANTENIMIENTO</div>
        <div class="content">
            El equipo <strong>%s</strong> ha alcanzado o superado el <strong>100%%</strong> de su frecuencia de mantenimiento.<br>
            <br>
            Modelo: <strong>%s</strong><br>
            Frecuencia por uso establecida: <strong>%d %s</strong><br>
            Cantidad actual: <strong>%.2f %s</strong><br>
            Frecuencia por tiempo establecida: <strong>%d meses</strong><br>
            Tiempo desde el último service: <strong>%.2f meses</strong>
        </div>
        <div class="footer">
            Este correo fue generado automáticamente por el sistema de mantenimiento.
        </div>
    </div>
</body>
</html>
""";


    private void verificarFrecuenciaYEnviarAlerta(Equipo equipo, ModeloEquipo modelo) {
        Double actual = equipo.getCantidadUnidadMedida();
        int frecuenciaUnidad = modelo.getFrecuenciaUnidadMedida();
        int frecuenciaTiempo = modelo.getFrecuenciaTiempo();

        if (frecuenciaUnidad == 0 || actual == null) return;

        try {
            // Obtener el último mantenimiento con esService = true, ordenado por fecha descendente
            List<Mantenimiento> mantenimientos = mantenimientoService.obtenerPorEquipo(equipo.getId());

            Mantenimiento ultimoService = mantenimientos.stream()
                    .filter(Mantenimiento::isEsService)
                    .max((m1, m2) -> m1.getFechaMantenimiento().compareTo(m2.getFechaMantenimiento()))
                    .orElse(null);

            // Cálculo por unidad de medida
            double valor = actual;
            if (ultimoService!=null)   valor = actual - ultimoService.getCantidadUnidadMedida();
            double porcentajeUnidad = (valor / frecuenciaUnidad) * 100;

            // Cálculo por tiempo (meses decimales)
            long mesesDecimales=0L;
            if(ultimoService!=null){
            LocalDate fechaUltimoService = ultimoService.getFechaMantenimiento()
                    .toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            LocalDate hoy = LocalDate.now();

            mesesDecimales = ChronoUnit.MONTHS.between(fechaUltimoService, hoy);
            }

            // Condiciones de alerta crítica y preventiva
            boolean esCriticoPorUso = porcentajeUnidad >= 100;
            boolean alertaPorUso = porcentajeUnidad >= 80 && porcentajeUnidad < 100;

            boolean esCriticoPorTiempo = mesesDecimales >= frecuenciaTiempo;
            boolean alertaPorTiempo = mesesDecimales >= (frecuenciaTiempo - 1) && mesesDecimales < frecuenciaTiempo;

            String html = null;
            boolean esCritico = false;

            if (esCriticoPorUso || esCriticoPorTiempo) {
                esCritico = true;
                html = String.format(htmlCritica,
                        equipo.getMatricula(),                                  // %s
                        modelo.getModelo(),                                     // %s
                        frecuenciaUnidad,                                       // %d
                        modelo.getUnidadMedida().name().toLowerCase(),         // %s
                        actual,                                                // %.2f
                        modelo.getUnidadMedida().name().toLowerCase(),         // %s
                        frecuenciaTiempo,                                       // %d
                        mesesDecimales                                         // %.2f
                );
            } else if (alertaPorUso || alertaPorTiempo) {
                html = String.format(htmlPreventiva,
                        equipo.getMatricula(),
                        modelo.getModelo(),
                        frecuenciaUnidad,
                        modelo.getUnidadMedida().name().toLowerCase(),
                        actual,
                        modelo.getUnidadMedida().name().toLowerCase(),
                        frecuenciaTiempo,
                        mesesDecimales
                );
            }

            if (html != null && equipo.getUnidad() != null && equipo.getUnidad().getEmails() != null) {
                for (UnidadEmail ue : equipo.getUnidad().getEmails()) {
                    emailService.enviarAlertaMantenimiento(
                            equipo,
                            modelo,
                            html,
                            esCritico,
                            ue.getEmail()
                    );
                }
            }

        } catch (Exception e) {
            throw new SigemaException("Error al enviár el email de alerta por cercanía de mantenimiento.");
        }
    }

    @Override
    public List<Equipo> obtenerEquiposPorIdModelo(Long idModelo, Long idUnidad) {
        if(idUnidad == null || idUnidad == 0){
            return equipoRepository.findByModeloEquipoId(idModelo);
        }else{
            return equipoRepository.findByModeloEquipoIdAndUnidad_Id(idModelo, idUnidad);
        }
    }
}

