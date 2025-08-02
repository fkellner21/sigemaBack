package com.example.sigema.services.implementations;

import com.example.sigema.models.Equipo;
import com.example.sigema.models.ModeloEquipo;
import com.example.sigema.models.Unidad;
import com.example.sigema.models.UnidadEmail;
import com.example.sigema.repositories.IEquipoRepository;
import com.example.sigema.services.IEquipoService;
import com.example.sigema.services.IModeloEquipoService;
import com.example.sigema.services.IUnidadService;
import com.example.sigema.utilidades.SigemaException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    //MENSAJES HTML
    String htmlPreventiva = """
<!DOCTYPE html>
<html>
<head>
    <style>
        body { font-family: Arial, sans-serif; background-color: #f9f9f9; padding: 20px; }
        .container { background-color: #ffffff; border-radius: 10px; padding: 20px; box-shadow: 0 2px 8px rgba(0,0,0,0.1); }
        .header { font-size: 22px; color: #ff9900; font-weight: bold; margin-bottom: 10px; }
        .content { font-size: 16px; color: #333; }
        .footer { margin-top: 20px; font-size: 12px; color: #999; }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">⚠️ Alerta preventiva</div>
        <div class="content">
            El equipo <strong>%s</strong> ha alcanzado el <strong>90%%</strong> de la frecuencia de mantenimiento.<br>
            <br>
            Modelo: <strong>%s</strong><br>
            Frecuencia establecida: <strong>%d</strong><br>
            Unidades actuales: <strong>%.2f</strong>
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
            Frecuencia por uso establecida: <strong>%d AGREGAR UNIDAD DE MEDIDA DEL MODELO EQUIPO</strong><br>
            Cantidad actual: <strong>%.2f AGREGAR UNIDAD DE MEDIDA DEL MODELO EQUIPO</strong>
            Frecuencia por tiempo establecida: <strong>%d AGREGAR TEXTO MESES</strong><br>
            Tiempo desde último service: <strong>%.2f AGERGAR TEXTO MESES, QUE SEA UN DECIMAL</strong>
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
        int frecuencia = modelo.getFrecuenciaUnidadMedida();

        if (frecuencia == 0 || actual == null) return;

        double porcentaje = (actual / frecuencia) * 100;

        String html = null;
        boolean esCritico = false;

        if (porcentaje >= 90 && porcentaje < 100) {
            html = String.format(htmlPreventiva,
                    equipo.getMatricula(),
                    modelo.getModelo(),
                    frecuencia,
                    actual
            );
        } else if (porcentaje >= 100) {
            html = String.format(htmlCritica,
                    equipo.getMatricula(),
                    modelo.getModelo(),
                    frecuencia,
                    actual
            );
            esCritico = true;
        }

        if (html != null) {
            Unidad unidad = equipo.getUnidad();
            if (unidad != null && unidad.getEmails() != null) {
                for (UnidadEmail ue : unidad.getEmails()) {
                    String destinatario = ue.getEmail();
                    emailService.enviarAlertaMantenimiento(
                            equipo,
                            modelo,
                            html,
                            esCritico,
                            destinatario
                    );
                }

            }
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

