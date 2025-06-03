package com.example.sigema.controllers;

import com.example.sigema.models.DocumentoModeloEquipo;
import com.example.sigema.models.ModeloEquipo;
import com.example.sigema.services.IDocumentoModeloEquipoService;
import com.example.sigema.services.IModeloEquipoService;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/modelosEquipo")
@CrossOrigin(origins = "*")
public class ModeloEquipoController {

    private final IModeloEquipoService modeloEquipoService;
    private final IDocumentoModeloEquipoService documentoService;

    private final String carpetaUploads = "uploads/documentos-modelo/";

    @Autowired
    public ModeloEquipoController(IModeloEquipoService modelEquipoService, IDocumentoModeloEquipoService documentoService) {
        this.modeloEquipoService = modelEquipoService;
        this.documentoService = documentoService;
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
    public ResponseEntity<String> subirDocumento(
            @PathVariable Long id,
            @RequestParam("archivo") MultipartFile archivo) {

        try {
            ModeloEquipo modelo = modeloEquipoService.ObtenerPorId(id).orElseThrow(() -> new RuntimeException("Modelo no encontrado"));;

            String nombreArchivo = UUID.randomUUID() + "_" + archivo.getOriginalFilename();
            Path ruta = Paths.get(carpetaUploads + nombreArchivo);
            Files.createDirectories(ruta.getParent());
            Files.write(ruta, archivo.getBytes());

            DocumentoModeloEquipo documento = new DocumentoModeloEquipo();
            documento.setNombreArchivo(archivo.getOriginalFilename());
            documento.setRutaArchivo(ruta.toString());
            documento.setModeloEquipo(modelo);

            documentoService.save(documento);

            return ResponseEntity.ok("Documento subido correctamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al subir el documento: " + e.getMessage());
        }
    }
    @GetMapping("/documentos/{id}/descargar")
    public ResponseEntity<Resource> descargar(@PathVariable Long id) throws IOException {
        DocumentoModeloEquipo doc = documentoService.findById(id)
                .orElseThrow(() -> new RuntimeException("Documento no encontrado"));

        Path path = Paths.get(doc.getRutaArchivo());
        Resource resource = new UrlResource(path.toUri());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + doc.getNombreArchivo() + "\"")
                .body(resource);
    }
}
