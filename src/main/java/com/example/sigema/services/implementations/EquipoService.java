package com.example.sigema.services.implementations;

import com.example.sigema.models.Equipo;
import com.example.sigema.models.Mantenimiento;
import com.example.sigema.models.ModeloEquipo;
import com.example.sigema.models.Unidad;
import com.example.sigema.repositories.IEquipoRepository;
import com.example.sigema.repositories.IMantenimientoRepository;
import com.example.sigema.services.IEquipoService;
import com.example.sigema.services.IModeloEquipoService;
import com.example.sigema.services.IUnidadService;
import com.example.sigema.utilidades.SigemaException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Service
@Transactional
public class EquipoService implements IEquipoService {

    private final IEquipoRepository equipoRepository;
    private final IModeloEquipoService modeloEquipoService;
    private final IUnidadService unidadService;
    private final IMantenimientoRepository mantenimientoRepository;

    @Autowired
    public EquipoService(IEquipoRepository equipoRepository, IModeloEquipoService modeloEquipoService, IUnidadService unidadService, IMantenimientoRepository mantenimientoRepository) {
        this.equipoRepository = equipoRepository;
        this.modeloEquipoService = modeloEquipoService;
        this.unidadService = unidadService;
        this.mantenimientoRepository = mantenimientoRepository;
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

        return equipoRepository.save(equipoEditar);
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
                    LocalDate.now().format(DateTimeFormatter.ofPattern("MMM.yy", Locale.of("es", "ES"))).toUpperCase());

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

        } catch (Exception ex) {
            throw new SigemaException("Ha ocurrido un error al generar el reporte de indicadores de gestión");
        }
    }

    private int generarTitulosExcel(XSSFWorkbook workbook, XSSFSheet sheet, int rowIndex, String[] columnas, String titulo) {
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
                fecha.getMonth().getDisplayName(TextStyle.FULL, new Locale("es", "ES")),
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
        for (int i = 0; i < columnas.length; i++) {
            XSSFCell cell = headerRow.createCell(i);
            cell.setCellValue(columnas[i]);
            cell.setCellStyle(headerStyle);
        }
        rowIndex++;

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
                cell9.setCellStyle(dateCellStyle);
            }

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
}