package com.example.sigema.controllers;

import com.example.sigema.models.DocumentoModeloEquipo;
import com.example.sigema.models.Equipo;
import com.example.sigema.models.ModeloEquipo;
import com.example.sigema.models.Repuesto;
import com.example.sigema.models.enums.TipoRepuesto;
import com.example.sigema.services.IDocumentoModeloEquipoService;
import com.example.sigema.services.IEquipoService;
import com.example.sigema.services.IModeloEquipoService;
import com.example.sigema.services.IRepuestoService;
import com.example.sigema.utilidades.SigemaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RestController
@RequestMapping("/api/modelosEquipo")
@CrossOrigin(origins = "*")
public class ModeloEquipoController {

    private final IModeloEquipoService modeloEquipoService;
    private final IDocumentoModeloEquipoService documentoService;
    private final IRepuestoService repuestoService;
    @Autowired
    private IEquipoService equipoService;

    private final String carpetaUploads = "uploads/documentos-modelo/";

    @Autowired
    public ModeloEquipoController(IModeloEquipoService modelEquipoService, IDocumentoModeloEquipoService documentoService,
                                  IRepuestoService repuestoService, IEquipoService equipoService) {
        this.modeloEquipoService = modelEquipoService;
        this.documentoService = documentoService;
        this.repuestoService = repuestoService;
        this.equipoService = equipoService;
    }

    @GetMapping("/{id}/repuestos/tipoRepuesto/{tipoRepuesto}")
    public ResponseEntity<?> obtenerTodos(@PathVariable Long id, @PathVariable TipoRepuesto tipoRepuesto) {
        try {
            List<Repuesto> repuestos = repuestoService.ObtenerTodos(id, tipoRepuesto);

            return ResponseEntity.ok().body(repuestos);
        } catch(SigemaException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().body("Ha ocurrido un error, vuelva a intentarlo");
        }
    }

    @GetMapping
    public ResponseEntity<?> obtenerTodos() {
        try {
            List<ModeloEquipo> modeloEquipos = modeloEquipoService.ObtenerTodos();
            return ResponseEntity.ok().body(modeloEquipos);
        } catch(SigemaException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().body("Ha ocurrido un error, vuelva a intentarlo");
        }
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody ModeloEquipo modeloEquipo) {
        try {
            ModeloEquipo model = modeloEquipoService.Crear(modeloEquipo);

            return ResponseEntity.ok().body(model);
        } catch(SigemaException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().body("Ha ocurrido un error, vuelva a intentarlo");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerModeloEquipo(@PathVariable Long id) {
        try {
            ModeloEquipo model = modeloEquipoService.ObtenerPorId(id).orElse(null);

            return ResponseEntity.ok().body(model);
        } catch(SigemaException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().body("Ha ocurrido un error, vuelva a intentarlo");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@PathVariable Long id, @RequestBody ModeloEquipo modeloEquipo) {
        try {
            ModeloEquipo model = modeloEquipoService.Editar(id, modeloEquipo);

            return ResponseEntity.ok().body(model);
        } catch(SigemaException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().body("Ha ocurrido un error, vuelva a intentarlo");
        }
    }

    @PostMapping("/{id}/documentos")
    public ResponseEntity<Map<String, String>>  subirDocumento(
            @PathVariable Long id,
            @RequestParam("archivo") MultipartFile archivo) {

        try {
            ModeloEquipo modelo = modeloEquipoService.ObtenerPorId(id).orElseThrow(() -> new SigemaException("Modelo no encontrado"));;

            String nombreArchivo = UUID.randomUUID() + "_" + archivo.getOriginalFilename();
            Path ruta = Paths.get(carpetaUploads+modelo.getId()+ '/' + nombreArchivo);
            Files.createDirectories(ruta.getParent());
            Files.write(ruta, archivo.getBytes());

            DocumentoModeloEquipo documento = new DocumentoModeloEquipo();
            documento.setNombreArchivo(archivo.getOriginalFilename());
            documento.setRutaArchivo(ruta.toString());
            documento.setModeloEquipo(modelo);

            documentoService.save(documento);

            Map<String, String> respuesta = new HashMap<>();
            respuesta.put("mensaje", "Documento subido correctamente");
            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al subir el documento: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    
    @GetMapping("/documentos/{id}/descargar")
    public ResponseEntity<Resource> descargar(@PathVariable Long id) throws Exception {
        DocumentoModeloEquipo doc = documentoService.findById(id)
                .orElseThrow(() -> new SigemaException("Documento no encontrado"));

        Path path = Paths.get(doc.getRutaArchivo());
        Resource resource = new UrlResource(path.toUri());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + doc.getNombreArchivo() + "\"")
                .body(resource);
    }
    @GetMapping("/{id}/documentos")
    public ResponseEntity<?> listarDocumentos(@PathVariable Long id) {
        try {
            ModeloEquipo modelo = modeloEquipoService.ObtenerPorId(id)
                    .orElseThrow(() -> new SigemaException("Modelo no encontrado"));

            List<DocumentoModeloEquipo> documentos = documentoService.findByModeloEquipo(modelo);
            return ResponseEntity.ok(documentos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener documentos: " + e.getMessage());
        }
    }

    @DeleteMapping("/documentos/{id}")
    public ResponseEntity<?> eliminarDocumento(@PathVariable Long id) {
        try {
            DocumentoModeloEquipo documento = documentoService.findById(id)
                    .orElseThrow(() -> new SigemaException("Documento no encontrado"));

            // Eliminar el archivo del sistema
            Path path = Paths.get(documento.getRutaArchivo());
            try {
                Files.deleteIfExists(path);
            } catch (IOException e) {
                // Si no se puede borrar, igual seguimos con el borrado en BD, pero logueamos
                System.err.println("No se pudo eliminar el archivo: " + e.getMessage());
            }

            // Eliminar de la base de datos
            documentoService.delete(documento);

            return ResponseEntity.ok().body(Map.of("mensaje", "Documento eliminado correctamente"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "No se pudo eliminar el documento: " + e.getMessage()));
        }
    }
    @GetMapping("/{id}/equipos")
    public ResponseEntity<?> obtenerEquiposPorModelo(@PathVariable Long id) {
        try {
            List<Equipo> equipos = equipoService.obtenerEquiposPorIdModelo(id);
            return ResponseEntity.ok(equipos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener los equipos del modelo: " + e.getMessage());
        }
    }


}
