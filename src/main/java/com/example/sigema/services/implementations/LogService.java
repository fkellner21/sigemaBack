package com.example.sigema.services.implementations;

import com.example.sigema.models.Usuario;
import com.example.sigema.repositories.IRepositoryUsuario;
import com.example.sigema.services.ILogService;
import com.example.sigema.utilidades.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;


@Service
public class LogService implements ILogService {
    private static final Logger logger = LoggerFactory.getLogger("SigemaLogs");
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final String LOG_FILE = "sigemalogs.txt";
    private static final String LOG_PATTERN = "sigemalogs.%s.0.txt.gz";

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private IRepositoryUsuario repositoryUsuario;

    @Override
    public void guardarLog(String mensaje, boolean mostrarUsuario) {
        String nombreUsuario = "anonimo";
        Usuario usuario;

        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                Long idUsuario = jwtUtils.extractIdUsuario(token);
                usuario = repositoryUsuario.findById(idUsuario).orElse(null);
                assert usuario != null;
                nombreUsuario = usuario.getNombreCompleto() + " (CI: " + usuario.getCedula() + ")";
            }
        } catch (Exception e) {

        }

        String fechaHora = LocalDateTime.now().format(formatter);
        String mensajeCompleto = "";

        if(mostrarUsuario){
            mensajeCompleto = fechaHora + " - Usuario: " + nombreUsuario + " - " + mensaje;
        }else{
            mensajeCompleto = fechaHora + " - " + mensaje;
        }

        logger.info(mensajeCompleto);
    }

    @Override
    public Resource descargarLogPorFecha(String fecha) throws IOException {
        String hoy = LocalDate.now().toString();

        if (fecha.equals(hoy)) {
            File logFile = new File(LOG_FILE);
            if (!logFile.exists()) {
                return null;
            }

            return new FileSystemResource(logFile);
        }

        String fileNameGz = String.format(LOG_PATTERN, fecha);
        File logFile = new File(fileNameGz);

        if (!logFile.exists()) {
            return null;
        }

        File txtFile = File.createTempFile("log_", ".txt");

        try (GZIPInputStream gzipIS = new GZIPInputStream(new FileInputStream(logFile));
             FileOutputStream fos = new FileOutputStream(txtFile)) {

            byte[] buffer = new byte[1024];
            int len;
            while ((len = gzipIS.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
        }

        return new FileSystemResource(txtFile);
    }

    @Override
    public List<String> listarLogsDisponibles() {
        File dir = new File(".");
        File[] archivos = dir.listFiles((d, name) -> name.startsWith("sigemalogs.") && (name.endsWith(".gz") || name.endsWith(".txt")));
        List<String> fechas = new ArrayList<>();

        if (archivos != null) {
            for (File f : archivos) {
                String nombre = f.getName();
                String[] partes = nombre.split("\\.");
                if (partes.length >= 3) {
                    fechas.add(partes[1]);
                }
            }
        }

        String hoy = LocalDate.now().toString();
        if (!fechas.contains(hoy)) {
            fechas.add(hoy);
        }

        fechas.sort(Comparator.reverseOrder());
        return fechas.stream().limit(10).toList();
    }
}