package com.example.sigema.services.implementations;

import com.example.sigema.models.*;
import com.example.sigema.models.enums.EstadoEquipo;
import com.example.sigema.models.enums.EstadoTramite;
import com.example.sigema.repositories.IEquipoRepository;
import com.example.sigema.repositories.IMantenimientoRepository;
import com.example.sigema.repositories.ITramitesRepository;
import com.example.sigema.services.*;
import com.example.sigema.utilidades.SigemaException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;

import java.io.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Stream;

@Service
@Transactional
public class EquipoService implements IEquipoService {

    private final IEquipoRepository equipoRepository;
    private final IModeloEquipoService modeloEquipoService;
    private final IUnidadService unidadService;
    private final IMantenimientoRepository mantenimientoRepository;
    private final ITramitesRepository tramitesRepository;
    private final EmailService emailService;
    private final ILogService logService;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public EquipoService(IEquipoRepository equipoRepository, IModeloEquipoService modeloEquipoService,
                         IUnidadService unidadService, IMantenimientoRepository mantenimientoRepository,
                         ITramitesRepository tramitesRepository, EmailService emailService, ILogService logService)
    {
        this.equipoRepository = equipoRepository;
        this.modeloEquipoService = modeloEquipoService;
        this.unidadService = unidadService;
        this.mantenimientoRepository = mantenimientoRepository;
        this.tramitesRepository = tramitesRepository;
        this.emailService = emailService;
        this.logService = logService;
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
    public EquipoActas Crear(Equipo equipo) throws Exception {
        equipo.validar();
        equipo.setActivo(true);
        EquipoActas equipoActas = new EquipoActas();
        Equipo equipoExistente = equipoRepository.findByMatricula(equipo.getMatricula().toUpperCase());
        if (equipoExistente != null) {
            throw new SigemaException("Ya existe un equipo con esa matrícula");
        }

        ModeloEquipo modeloEquipo = modeloEquipoService.ObtenerPorId(equipo.getIdModeloEquipo()).orElse(null);

        if (modeloEquipo == null) {
            throw new SigemaException("El modelo de equipo ingresado no existe");
        }

        Long idUnidad = equipo.getIdUnidad();

        if (equipo.getUnidad() != null && equipo.getUnidad().getId() != 0) {
            idUnidad = equipo.getUnidad().getId();
        }

        Unidad unidad = unidadService.ObtenerPorId(idUnidad).orElse(null);

        if (unidad == null) {
            throw new SigemaException("La unidad ingresada no existe");
        }

        equipo.setModeloEquipo(modeloEquipo);
        equipo.setUnidad(unidad);
        equipo.setMatricula(equipo.getMatricula().toUpperCase());
        equipo.setLatitud(unidad.getLatitud());
        equipo.setLongitud(unidad.getLongitud());
        equipo.setFechaUltimaPosicion(new Date());

        equipoActas.setEquipo(equipoRepository.save(equipo));

        List<ReporteActa> actas = new ArrayList<>();
        actas.add(generarActaEquipo(equipo, true));

        equipoActas.setActas(actas);
        logService.guardarLog("Se ha creado el equipo (Matricula: " + equipo.getMatricula() + ", Modelo: " + equipo.getModeloEquipo().getModelo() + ")", true);

        return equipoActas;
    }

    @Override
    public EquipoActas Eliminar(Long id) throws Exception {
        Equipo equipo = ObtenerPorId(id);

        equipo.setActivo(false);
        equipo.setMatricula(null);
        logService.guardarLog("Se ha eliminado el equipo (Matricula: " + equipo.getMatricula() + ", Modelo: " + equipo.getModeloEquipo().getModelo() + ")", true);

        return Editar(id, equipo);
    }

    @Override
    public Equipo ObtenerPorId(Long id) throws Exception {
        return equipoRepository.findById(id).map(e -> {
            entityManager.refresh(e);
            return e;
        }).orElse(null);
    }

    @Override
    public EquipoActas Editar(Long id, Equipo equipo) throws Exception {
        equipo.validar();
        EquipoActas equipoActas = new EquipoActas();
        List<ReporteActa> actas = new ArrayList<>();
        Long idModelo = equipo.getIdModeloEquipo();
        boolean eliminarEquipo = equipo.isActivo();
        double lat = equipo.getLatitud();
        double lon = equipo.getLongitud();
        double cant = equipo.getCantidadUnidadMedida();
        String numeroMotor = equipo.getNumeroMotor();

        if (idModelo == null || idModelo == 0) {
            ModeloEquipo me = equipo.getModeloEquipo();
            idModelo = (me != null) ? me.getId() : null;
        }

        if (idModelo == null || idModelo == 0) {
            throw new SigemaException("Debe asociar un modelo válido al equipo");
        }

        ModeloEquipo modeloEquipo = modeloEquipoService.ObtenerPorId(idModelo)
                .orElseThrow(() -> new SigemaException("Modelo de equipo no encontrado"));

        Equipo equipoEditar = equipoRepository.findById(id)
                .orElseThrow(() -> new SigemaException("El equipo no existe"));

        entityManager.refresh(equipoEditar);

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

        if(!Objects.equals(equipoEditar.getUnidad().getId(), unidad.getId())){
            actas.add(generarActaEquipo(equipoEditar, false));
        }

        if(equipoEditar.isActivo() != eliminarEquipo){
            actas.add(generarActaEquipo(equipoEditar, eliminarEquipo));
        }

        equipoEditar.setEstado(equipo.getEstado());
        equipoEditar.setCantidadUnidadMedida(cant);
        equipoEditar.setMatricula(equipo.getMatricula() != null ? equipo.getMatricula().toUpperCase() : null);
        equipoEditar.setUnidad(unidad);
        equipoEditar.setIdModeloEquipo(idModelo);
        equipoEditar.setObservaciones(equipo.getObservaciones());
        equipoEditar.setActivo(eliminarEquipo);
        equipoEditar.setLatitud(lat);
        equipoEditar.setLongitud(lon);
        equipoEditar.setNumeroMotor((numeroMotor));
        equipoRepository.save(equipoEditar);

        equipoActas.setEquipo(equipoEditar);
        equipoActas.setActas(actas);

        verificarFrecuenciaYEnviarAlerta(equipoEditar, modeloEquipo);
        ObjectMapper mapper = new ObjectMapper();

        logService.guardarLog("Se ha editado el equipo (Matricula: " + equipo.getMatricula() + ", Modelo: " + equipo.getModeloEquipo().getModelo() + ") ", true);

        return equipoActas;
    }

    @Override
    public List<Equipo> obtenerEquiposPorIdModelo(Long idModelo, Long idUnidad) {
        if(idUnidad == null || idUnidad == 0){
            return equipoRepository.findByModeloEquipoId(idModelo);
        }else{
            return equipoRepository.findByModeloEquipoIdAndUnidad_Id(idModelo, idUnidad);
        }
    }

    public void GenerarExcelIndicadoresGestion(HttpServletResponse response, Long idUnidad) throws SigemaException {
        try {
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("INDICADORES DE GESTIÓN");
            List<Equipo> equipos = obtenerTodos(idUnidad);
            String[] columnas = {
                    "UNIDAD", "MATRÍCULA", "EQUIPO", "CAPACIDAD", "MARCA",
                    "MODELO", "AÑO", "ESTADO", "HORAS/KMS", "ÚLTIMO MANTENIMIENTO", "OBSERVACIONES"
            };

            if (idUnidad == null || idUnidad == 0) {
                equipos = equipos.stream()
                        .sorted(Comparator.comparing(e -> e.getUnidad().getId()))
                        .toList();
            }

            int rowIndex = generarTitulosExcel(workbook, sheet, 0, columnas, "INDICADORES DE GESTIÓN DEL EQUIPAMIENTO DE INGENIEROS " +
                    LocalDate.now().format(DateTimeFormatter.ofPattern("MMM.yy", Locale.of("es", "ES"))).toUpperCase(), true);

            generarFilasExcelIndicadoresGestion(workbook, sheet, equipos, rowIndex);

            int headerRowIndex = rowIndex - 1;
            int lastRowWithData = rowIndex + equipos.size() - 1;
            sheet.setAutoFilter(new CellRangeAddress(headerRowIndex, lastRowWithData, 0, columnas.length - 1));

            for (int i = 0; i < columnas.length; i++) {
                sheet.autoSizeColumn(i);
                int currentWidth = sheet.getColumnWidth(i);
                sheet.setColumnWidth(i, (int) (currentWidth * 1.7));
            }

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=INDICADORES_DE_GESTIÓN.xlsx");

            workbook.write(response.getOutputStream());
            workbook.close();

            logService.guardarLog("Se ha generado el reporte de indicadores de gestión", true);
        } catch (Exception ex) {
            throw new SigemaException("Ha ocurrido un error al generar el reporte de indicadores de gestión");
        }
    }

    private int generarTitulosExcel(XSSFWorkbook workbook, XSSFSheet sheet, int rowIndex, String[] columnas, String titulo, boolean mostrarColumnas) {
        XSSFCellStyle titleStyle = workbook.createCellStyle();
        XSSFFont titleFont = workbook.createFont();
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short) 15);
        titleStyle.setFont(titleFont);
        titleStyle.setAlignment(HorizontalAlignment.CENTER);

        XSSFCellStyle subtitleStyle = workbook.createCellStyle();
        XSSFFont subtitleFont = workbook.createFont();
        subtitleFont.setBold(true);
        subtitleFont.setFontHeightInPoints((short) 13);
        subtitleStyle.setFont(subtitleFont);

        XSSFRow titleRow1 = sheet.createRow(rowIndex);
        XSSFCell titleCell1 = titleRow1.createCell(0);
        titleCell1.setCellValue("ÁREA LOGÍSTICA DE INGENIEROS");
        titleCell1.setCellStyle(subtitleStyle);
        sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 0, columnas.length - 1));
        rowIndex++;

        LocalDate fecha = LocalDate.now();
        String fechaFormateada = String.format(
                "%d de %s de %d",
                fecha.getDayOfMonth(),
                fecha.getMonth().getDisplayName(TextStyle.FULL, Locale.forLanguageTag("es-ES")),
                fecha.getYear()
        );

        XSSFRow titleRow2 = sheet.createRow(rowIndex);
        XSSFCell titleCell2 = titleRow2.createCell(0);
        titleCell2.setCellValue("Paso Carrasco, " + fechaFormateada);
        titleCell2.setCellStyle(subtitleStyle);
        sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 0, columnas.length - 1));

        rowIndex++;
        rowIndex++;

        XSSFRow titleRow3 = sheet.createRow(rowIndex);
        XSSFCell titleCell3 = titleRow3.createCell(0);
        titleCell3.setCellValue(titulo);
        titleCell3.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 0, columnas.length - 1));

        rowIndex++;
        rowIndex++;

        XSSFCellStyle headerStyle = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 13);
        headerStyle.setFont(font);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        addBorders(headerStyle);

        XSSFRow headerRow = sheet.createRow(rowIndex);
        headerRow.setHeightInPoints(25);

        if(mostrarColumnas) {
            for (int i = 0; i < columnas.length; i++) {
                XSSFCell cell = headerRow.createCell(i);
                cell.setCellValue(columnas[i]);
                cell.setCellStyle(headerStyle);
            }
            rowIndex++;
        }

        return rowIndex;
    }

    private void generarFilasExcelIndicadoresGestion(XSSFWorkbook workbook, XSSFSheet sheet, List<Equipo> equipos, int rowIndex){
        XSSFFont dataFont = workbook.createFont();
        dataFont.setFontHeightInPoints((short) 12);
        dataFont.setBold(false);

        CreationHelper createHelper = workbook.getCreationHelper();
        XSSFCellStyle dateCellStyle = workbook.createCellStyle();
        dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd/MM/yyyy"));
        dateCellStyle.setAlignment(HorizontalAlignment.CENTER);
        dateCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        dateCellStyle.setFont(dataFont);
        addBorders(dateCellStyle);

        XSSFCellStyle centeredStyle = workbook.createCellStyle();
        centeredStyle.setAlignment(HorizontalAlignment.CENTER);
        centeredStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        centeredStyle.setFont(dataFont);
        addBorders(centeredStyle);

        XSSFCellStyle normalStyle = workbook.createCellStyle();
        normalStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        normalStyle.setFont(dataFont);
        addBorders(normalStyle);

        for (int i = 0; i < equipos.size(); i++) {
            Equipo equipo = equipos.get(i);
            Mantenimiento ultimoMantenimiento = mantenimientoRepository.findTopByEquipo_IdOrderByFechaMantenimientoDesc(equipo.getId()).orElse(null);

            XSSFRow row = sheet.createRow(rowIndex + i);
            row.setHeightInPoints(25);

            XSSFCell cell0 = row.createCell(0);
            cell0.setCellValue(equipo.getUnidad() != null && equipo.getUnidad().getNombre() != null ? equipo.getUnidad().getNombre() : "---");
            cell0.setCellStyle(normalStyle);

            XSSFCell cell1 = row.createCell(1);
            cell1.setCellValue(equipo.getMatricula() != null ? equipo.getMatricula() : "---");
            cell1.setCellStyle(normalStyle);

            XSSFCell cell2 = row.createCell(2);
            cell2.setCellValue(equipo.getModeloEquipo() != null && equipo.getModeloEquipo().getModelo() != null ? equipo.getModeloEquipo().getModelo() : "---");
            cell2.setCellStyle(normalStyle);

            XSSFCell cell3 = row.createCell(3);
            if (equipo.getModeloEquipo() != null) {
                cell3.setCellValue(equipo.getModeloEquipo().getCapacidad() + " ");
                cell3.setCellStyle(centeredStyle);
            } else {
                cell3.setCellValue("---");
            }

            XSSFCell cell4 = row.createCell(4);
            cell4.setCellValue(equipo.getModeloEquipo() != null && equipo.getModeloEquipo().getMarca() != null && equipo.getModeloEquipo().getMarca().getNombre() != null ?
                    equipo.getModeloEquipo().getMarca().getNombre() : "---");
            cell4.setCellStyle(centeredStyle);

            XSSFCell cell5 = row.createCell(5);
            if (equipo.getModeloEquipo() != null) {
                cell5.setCellValue(equipo.getModeloEquipo().getModelo());
                cell5.setCellStyle(centeredStyle);
            } else {
                cell5.setCellValue("---");
            }

            XSSFCell cell6 = row.createCell(6);
            if (equipo.getModeloEquipo() != null) {
                cell6.setCellValue(equipo.getModeloEquipo().getAnio());
                cell6.setCellStyle(centeredStyle);
            } else {
                cell6.setCellValue("---");
            }

            XSSFCell cell7 = row.createCell(7);
            cell7.setCellValue(equipo.getEstado() != null ? equipo.getEstado().name() : "---");
            cell7.setCellStyle(centeredStyle);

            XSSFCell cell8 = row.createCell(8);
            String cantidadUnidadMedida = "---";
            if (equipo.getModeloEquipo() != null && equipo.getModeloEquipo().getUnidadMedida() != null) {
                cantidadUnidadMedida = equipo.getCantidadUnidadMedida() + " " + equipo.getModeloEquipo().getUnidadMedida().name();
            }
            cell8.setCellValue(cantidadUnidadMedida);
            cell8.setCellStyle(centeredStyle);

            XSSFCell cell9 = row.createCell(9);
            if (ultimoMantenimiento != null && ultimoMantenimiento.getFechaMantenimiento() != null) {
                cell9.setCellValue(ultimoMantenimiento.getFechaMantenimiento());
            }
            cell9.setCellStyle(dateCellStyle);

            XSSFCell cell10 = row.createCell(10);
            cell10.setCellValue(equipo.getObservaciones() != null ? equipo.getObservaciones() : "");
            cell10.setCellStyle(normalStyle);
        }
    }

    private void addBorders(XSSFCellStyle style) {
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setTopBorderColor(IndexedColors.BLACK.getIndex());
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        style.setRightBorderColor(IndexedColors.BLACK.getIndex());
    }

    @Override
    public ReporteActa generarActaEquipo(Equipo equipo, boolean esDotacion) throws IOException {
        ClassPathResource plantillaResource = new ClassPathResource("recursos/PlantillaReporteEquipo.docx");

        LocalDate fecha = LocalDate.now();
        String fechaFormateada = String.format(
                "%d de %s de %d",
                fecha.getDayOfMonth(),
                fecha.getMonth().getDisplayName(TextStyle.FULL, Locale.forLanguageTag("es-ES")),
                fecha.getYear()
        );

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMMyy", Locale.forLanguageTag("es-ES"));
        String fechaFormateadaEquipo = fecha.format(formatter).toUpperCase();

        try (InputStream plantillaStream = plantillaResource.getInputStream();
             XWPFDocument doc = new XWPFDocument(plantillaStream);
             ByteArrayOutputStream outStream = new ByteArrayOutputStream()) {

            Map<String, String> reemplazos = Map.of(
                    "{textoFecha}", fechaFormateada,
                    "{textoActa}", esDotacion ? "ALTA" : "BAJA",
                    "{textoCaracter}", esDotacion ? "Dotación" : "Baja",
                    "{textoEquipo}", equipo.getModeloEquipo() != null && equipo.getModeloEquipo().getTipoEquipo() != null
                            ? equipo.getModeloEquipo().getTipoEquipo().getNombre() : "",
                    "{textoMatricula}", equipo.getMatricula() != null ? equipo.getMatricula() : "",
                    "{textoMarca}", equipo.getModeloEquipo() != null && equipo.getModeloEquipo().getMarca() != null
                            ? equipo.getModeloEquipo().getMarca().getNombre() : "",
                    "{textoFechaEquipo}", fechaFormateadaEquipo,
                    "{textoModelo}", equipo.getModeloEquipo() != null ? equipo.getModeloEquipo().getModelo() : "",
                    "{textoMotor}", equipo.getNumeroMotor() != null ? equipo.getNumeroMotor() : "",
                    "{textoUnidad}", equipo.getUnidad() != null ? equipo.getUnidad().getNombre() : ""
            );

            for (Map.Entry<String, String> entry : reemplazos.entrySet()) {
                for (XWPFParagraph p : doc.getParagraphs()) {
                    reemplazarTexto(p, entry.getKey(), entry.getValue());
                }

                reemplazarTextoEnTablas(doc, entry.getKey(), entry.getValue());
            }

            doc.write(outStream);

            ReporteActa reporteActa = new ReporteActa();
            String nombreArchivo = "Acta de " + (esDotacion ? "Alta" : "Baja") + " de Equipo_" + fechaFormateadaEquipo + ".docx";
            reporteActa.setNombre(nombreArchivo);
            reporteActa.setArchivo(outStream.toByteArray());

            logService.guardarLog("Se ha generado el acta de " + (esDotacion ? "Alta" : "Baja") + " del equipo (Matricula: " + equipo.getMatricula() + ", Modelo: " + equipo.getModeloEquipo().getModelo() + ")", true);

            return reporteActa;
        }
    }

    private void reemplazarTexto(XWPFParagraph paragraph, String buscar, String reemplazo) {
        if (reemplazo == null) {
            reemplazo = "";
        }

        if (paragraph == null || buscar == null || buscar.isEmpty()) {
            return;
        }

        List<XWPFRun> runs = paragraph.getRuns();
        if (runs == null || runs.isEmpty()) {
            return;
        }

        StringBuilder paragraphText = new StringBuilder();
        for (XWPFRun run : runs) {
            String text = run.getText(0);
            if (text != null) {
                paragraphText.append(text);
            }
        }

        String textoCompleto = paragraphText.toString();

        if (!textoCompleto.contains(buscar)) {
            return;
        }

        textoCompleto = textoCompleto.replace(buscar, reemplazo);

        for (int i = runs.size() - 1; i >= 0; i--) {
            paragraph.removeRun(i);
        }

        paragraph.createRun().setText(textoCompleto);
    }

    private void reemplazarTextoEnTablas(XWPFDocument doc, String buscar, String reemplazo) {
        for (XWPFTable tabla : doc.getTables()) {
            for (XWPFTableRow fila : tabla.getRows()) {
                for (XWPFTableCell celda : fila.getTableCells()) {
                    for (XWPFParagraph p : celda.getParagraphs()) {
                        reemplazarTexto(p, buscar, reemplazo);
                    }
                }
            }
        }
    }

    public void generarExcelInformeAnioProximo(HttpServletResponse response, Long idUnidad) throws SigemaException {
        try {
            XSSFWorkbook workbook = new XSSFWorkbook();

            String[] columnasBase = {"TIPO", "CÓDIGO SICE", "REPUESTO", "CANTIDAD", "CARACTERISTICAS", "OBSERVACIONES"};
            String[] columnasEquipo = {"UNIDAD", "TIPO EQUIPO", "MATRICULA", "ESTADO"};
            String[] columnasCompletas = Stream.concat(Arrays.stream(columnasEquipo), Arrays.stream(columnasBase)).toArray(String[]::new);
            String[] columnasRepuestos = Arrays.stream(columnasCompletas).filter(c -> !c.equals("CANTIDAD")).toArray(String[]::new);

            List<Equipo> equipos = obtenerTodos(idUnidad);

            XSSFCellStyle titleStyle = workbook.createCellStyle();
            XSSFFont titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 13);
            titleStyle.setFont(titleFont);
            titleStyle.setAlignment(HorizontalAlignment.CENTER);

            XSSFSheet sheetMantenimientos = workbook.createSheet("MANTENIMIENTOS PREVENTIVOS");
            int rowIndexMantenimientos = generarTitulosExcel(workbook, sheetMantenimientos, 0, columnasCompletas,
                    "INFORME MANTENIMIENTOS PREVENTIVOS " +
                            LocalDate.now().format(DateTimeFormatter.ofPattern("MMM.yy", new Locale("es", "ES"))).toUpperCase(), true);

            XSSFSheet sheetRepuestos = workbook.createSheet("REPUESTOS SOLICITADOS");
            int rowIndexRepuestos = generarTitulosExcel(workbook, sheetRepuestos, 0, columnasRepuestos,
                    "INFORME REPUESTOS SOLICITADOS " +
                            LocalDate.now().format(DateTimeFormatter.ofPattern("MMM.yy", new Locale("es", "ES"))).toUpperCase(), true);

            List<EstadoTramite> estadosTramites = List.of(EstadoTramite.Iniciado, EstadoTramite.EnTramite);

            XSSFCellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setAlignment(HorizontalAlignment.LEFT);
            dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            addBorders(dataStyle);

            Map<String, ResumenRepuesto> resumenRepuestos = new HashMap<>();

            for (Equipo e : equipos) {
                boolean tieneMantenimientos = e.getModeloEquipo() != null
                        && e.getModeloEquipo().getServiceModelo() != null
                        && !e.getModeloEquipo().getServiceModelo().getRepuestosMantenimiento().isEmpty();

                List<Tramite> tramites = tramitesRepository.findByEquipo_IdAndEstadoIn(e.getId(), estadosTramites);
                boolean tieneRepuestosSolicitados = !tramites.isEmpty();

                if (tieneMantenimientos) {
                    int frecuenciaMeses = e.getModeloEquipo().getFrecuenciaTiempo();
                    List<RepuestoMantenimiento> repuestosMantenimiento = e.getModeloEquipo().getServiceModelo().getRepuestosMantenimiento();

                    for (RepuestoMantenimiento rm : repuestosMantenimiento) {
                        Repuesto repuesto = rm.getRepuesto();
                        double cantidad = rm.getCantidadUsada() * (frecuenciaMeses <= 6 ? 2 : 1);

                        XSSFRow row = sheetMantenimientos.createRow(rowIndexMantenimientos++);
                        int col = 0;
                        row.createCell(col++).setCellValue(e.getUnidad().getNombre());
                        row.createCell(col++).setCellValue(e.getModeloEquipo().getTipoEquipo().getCodigo());
                        row.createCell(col++).setCellValue(e.getMatricula());
                        row.createCell(col++).setCellValue(e.getEstado().toString());

                        row.createCell(col++).setCellValue(repuesto.getTipo().toString());
                        row.createCell(col++).setCellValue(repuesto.getCodigoSICE());
                        row.createCell(col++).setCellValue(repuesto.getNombre());
                        row.createCell(col++).setCellValue(cantidad);
                        row.createCell(col++).setCellValue(repuesto.getCaracteristicas());
                        row.createCell(col).setCellValue(repuesto.getObservaciones());

                        for (int i = 0; i < columnasCompletas.length; i++) {
                            row.getCell(i).setCellStyle(dataStyle);
                        }

                        resumenRepuestos.merge(repuesto.getCodigoSICE(),
                                new ResumenRepuesto(repuesto, cantidad),
                                (existente, nuevo) -> {
                                    existente.agregarCantidad(nuevo.cantidad);
                                    return existente;
                                });
                    }
                }

                if (tieneRepuestosSolicitados) {
                    for (Tramite tramite : tramites) {
                        Repuesto repuesto = tramite.getRepuesto();
                        if (repuesto != null) {
                            XSSFRow row = sheetRepuestos.createRow(rowIndexRepuestos++);
                            int col = 0;
                            row.createCell(col++).setCellValue(e.getUnidad().getNombre());
                            row.createCell(col++).setCellValue(e.getModeloEquipo().getTipoEquipo().getCodigo());
                            row.createCell(col++).setCellValue(e.getMatricula());
                            row.createCell(col++).setCellValue(e.getEstado().toString());

                            row.createCell(col++).setCellValue(repuesto.getTipo().toString());
                            row.createCell(col++).setCellValue(repuesto.getCodigoSICE());
                            row.createCell(col++).setCellValue(repuesto.getNombre());
                            row.createCell(col++).setCellValue(repuesto.getCaracteristicas());
                            row.createCell(col).setCellValue(repuesto.getObservaciones());

                            for (int i = 0; i < columnasRepuestos.length; i++) {
                                row.getCell(i).setCellStyle(dataStyle);
                            }

                            resumenRepuestos.merge(repuesto.getCodigoSICE(),
                                    new ResumenRepuesto(repuesto, repuesto.getCantidad()),
                                    (existente, nuevo) -> {
                                        existente.agregarCantidad(nuevo.cantidad);
                                        return existente;
                                    });
                        }
                    }
                }
            }

            XSSFSheet sheetResumen = workbook.createSheet("RESUMEN REPUESTOS");
            int rowIndexResumen = 0;
            String[] columnasResumen = {"CÓDIGO SICE", "REPUESTO", "TIPO", "CARACTERISTICAS", "CANTIDAD TOTAL"};
            rowIndexResumen = generarTitulosExcel(workbook, sheetResumen, rowIndexResumen, columnasResumen,
                    "RESUMEN DE REPUESTOS AGRUPADOS POR CÓDIGO SICE", true);

            List<ResumenRepuesto> resumenOrdenado = resumenRepuestos.values().stream()
                    .sorted(Comparator.comparing(r -> r.codigoSice))
                    .toList();

            for (ResumenRepuesto r : resumenOrdenado) {
                XSSFRow row = sheetResumen.createRow(rowIndexResumen++);
                int col = 0;
                row.createCell(col++).setCellValue(r.codigoSice);
                row.createCell(col++).setCellValue(r.nombre);
                row.createCell(col++).setCellValue(r.tipo);
                row.createCell(col++).setCellValue(r.caracteristicas);
                row.createCell(col).setCellValue(r.cantidad);

                for (int i = 0; i < columnasResumen.length; i++) {
                    row.getCell(i).setCellStyle(dataStyle);
                }
            }

            sheetMantenimientos.setAutoFilter(new CellRangeAddress(5, rowIndexMantenimientos - 1, 0, columnasCompletas.length - 1));
            sheetRepuestos.setAutoFilter(new CellRangeAddress(5, rowIndexRepuestos - 1, 0, columnasRepuestos.length - 1));
            sheetResumen.setAutoFilter(new CellRangeAddress(5, rowIndexResumen - 1, 0, columnasResumen.length - 1));

            for (int i = 0; i < columnasCompletas.length; i++) {
                sheetMantenimientos.autoSizeColumn(i);
                int widthM = sheetMantenimientos.getColumnWidth(i);
                sheetMantenimientos.setColumnWidth(i, (int) (widthM * 1.5));
            }

            for (int i = 0; i < columnasRepuestos.length; i++) {
                sheetRepuestos.autoSizeColumn(i);
                int widthR = sheetRepuestos.getColumnWidth(i);
                sheetRepuestos.setColumnWidth(i, (int) (widthR * 1.5));
            }

            for (int i = 0; i < columnasResumen.length; i++) {
                sheetResumen.autoSizeColumn(i);
                int width = sheetResumen.getColumnWidth(i);
                sheetResumen.setColumnWidth(i, (int) (width * 1.5));
            }

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=INFORME_PREVISIONES.xlsx");

            workbook.write(response.getOutputStream());
            workbook.close();
            logService.guardarLog("Se ha generado el reporte de informe de previsiones", true);
        } catch (Exception ex) {
            throw new SigemaException("Ha ocurrido un error al generar el informe de repuestos para el año próximo");
        }
    }

    private static class ResumenRepuesto {
        String codigoSice;
        String nombre;
        String tipo;
        String caracteristicas;
        double cantidad;

        public ResumenRepuesto(Repuesto r, double cantidadInicial) {
            this.codigoSice = r.getCodigoSICE();
            this.nombre = r.getNombre();
            this.tipo = r.getTipo().toString();
            this.caracteristicas = r.getCaracteristicas();
            this.cantidad = cantidadInicial;
        }

        public void agregarCantidad(double cantidadExtra) {
            this.cantidad += cantidadExtra;
        }
    }

    private void crearEncabezadoTabla(XSSFWorkbook workbook, XSSFSheet sheet, String[] columnas, XSSFCellStyle style) {
        XSSFRow headerRow = sheet.createRow(0);
        for (int i = 0; i < columnas.length; i++) {
            XSSFCell cell = headerRow.createCell(i);
            cell.setCellValue(columnas[i]);
            cell.setCellStyle(style);
        }
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
            El equipo <strong>%s</strong> ha superado el <strong>80%%</strong> de su frecuencia de mantenimiento.

            

            Modelo: <strong>%s</strong>

            Frecuencia por uso establecida: <strong>%d %s</strong>

            Cantidad actual: <strong>%.2f %s</strong>

            Frecuencia por tiempo establecida: <strong>%d meses</strong>

            Tiempo desde el último service: <strong>%d meses y %d días</strong>
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
            El equipo <strong>%s</strong> ha alcanzado o superado el <strong>100%%</strong> de su frecuencia de mantenimiento.

            

            Modelo: <strong>%s</strong>

            Frecuencia por uso establecida: <strong>%d %s</strong>

            Cantidad actual: <strong>%.2f %s</strong>

            Frecuencia por tiempo establecida: <strong>%d meses</strong>

            Tiempo desde el último service: <strong>%d meses y %d días</strong>
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
            List<Mantenimiento> mantenimientos = mantenimientoRepository.findByEquipo_IdOrderByFechaMantenimientoDesc(equipo.getId());

            Mantenimiento ultimoService = mantenimientos.stream()
                    .filter(Mantenimiento::isEsService)
                    .max((m1, m2) -> m1.getFechaMantenimiento().compareTo(m2.getFechaMantenimiento()))
                    .orElse(null);

            // Cálculo por unidad de medida
            double valor = actual;
            if (ultimoService!=null)   valor = actual - ultimoService.getCantidadUnidadMedida();
            double porcentajeUnidad = (valor / frecuenciaUnidad) * 100;

            // Cálculo por tiempo (meses decimales)
            float mesesDecimales=0f;
            long mesesCompletos = 0;
            long diasExtra = 0;
            if(ultimoService!=null){
                LocalDate fechaUltimoService = ultimoService.getFechaMantenimiento()
                        .toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

                LocalDate hoy = LocalDate.now();
                mesesCompletos = ChronoUnit.MONTHS.between(fechaUltimoService, hoy);
                LocalDate fechaMasMeses = fechaUltimoService.plusMonths(mesesCompletos);
                diasExtra = ChronoUnit.DAYS.between(fechaMasMeses, hoy);
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
                        equipo.getMatricula(),
                        modelo.getModelo(),
                        frecuenciaUnidad,
                        modelo.getUnidadMedida().name().toLowerCase(),
                        actual,
                        modelo.getUnidadMedida().name().toLowerCase(),
                        frecuenciaTiempo,
                        mesesCompletos,
                        diasExtra
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
                        mesesCompletos,
                        diasExtra
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

                logService.guardarLog("Se ha enviado la alerta de mantenimiento para el equipo (Matricula: " + equipo.getMatricula() + ", Modelo: " + equipo.getModeloEquipo().getModelo() + ")", false);
            }
        } catch (Exception e) {
            throw new SigemaException("Error al enviár el email de alerta por cercanía de mantenimiento.");
        }
    }
}