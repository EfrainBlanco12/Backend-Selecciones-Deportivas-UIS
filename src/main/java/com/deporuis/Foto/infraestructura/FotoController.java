package com.deporuis.Foto.infraestructura;

import com.deporuis.Foto.aplicacion.FotoService;
import com.deporuis.Foto.infraestructura.dto.FotoRequest;
import com.deporuis.Foto.infraestructura.dto.FotoResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/private/foto")
@Controller
public class FotoController {

    @Autowired
    private FotoService fotoService;

    /**
     * Crear fotos (POST /foto/crear)
     */
    @PostMapping("/crear")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ENTRENADOR')")
    public ResponseEntity<FotoResponse> crearFoto(@Valid @RequestBody FotoRequest fotoRequest) {
        FotoResponse fotoCreada = fotoService.crearFoto(fotoRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(fotoCreada);
    }

    /**
     * Obtener una sola foto por su ID (GET /foto/obtener/{id})
     */
    @GetMapping("/obtener/{id}")
    public ResponseEntity<FotoResponse> obtenerFoto(
            @PathVariable Integer id
    ) {
        FotoResponse foto = fotoService.obtenerFoto(id);
        if (foto == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(foto);
    }

    /**
     * Obtener fotos por paginas, definiendo el numero de la pagina y su tamaño (GET /foto/lista)
     */
    @GetMapping("/lista")
    public ResponseEntity<Page<FotoResponse>> obtenerFotosPaginadas(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "5") Integer size
    ) {
        Page<FotoResponse> pagina = fotoService.obtenerFotosPaginadas(page, size);
        return ResponseEntity.ok(pagina);
    }

    /**
     * Obtener fotos por integrante (GET /foto/integrante/{idIntegrante})
     */
    @GetMapping("/integrante/{idIntegrante}")
    public ResponseEntity<List<FotoResponse>> obtenerFotosPorIntegrante(
            @PathVariable Integer idIntegrante
    ) {
        List<FotoResponse> fotos = fotoService.obtenerFotosPorIntegrante(idIntegrante);
        return ResponseEntity.ok(fotos);
    }

    /**
     * Obtener fotos por selección (GET /foto/seleccion/{idSeleccion})
     */
    @GetMapping("/seleccion/{idSeleccion}")
    public ResponseEntity<List<FotoResponse>> obtenerFotosPorSeleccion(
            @PathVariable Integer idSeleccion
    ) {
        List<FotoResponse> fotos = fotoService.obtenerFotosPorSeleccion(idSeleccion);
        return ResponseEntity.ok(fotos);
    }

    /**
     * Actualizar una foto existente (PUT /foto/actualizar/{idFoto})
     */
    @PutMapping("/actualizar/{idFoto}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ENTRENADOR')")
    public ResponseEntity<FotoResponse> actualizarFoto(
            @PathVariable Integer idFoto,
            @Valid @RequestBody FotoRequest fotoRequest
    ) {
        FotoResponse fotoActualizada = fotoService.actualizarFoto(idFoto, fotoRequest);
        return ResponseEntity.ok(fotoActualizada);
    }

    /**
     * Eliminar una foto (DELETE /foto/eliminar/{idFoto})
     */
    @DeleteMapping("/eliminar/{idFoto}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ENTRENADOR')")
    public ResponseEntity<Void> eliminarFoto(@PathVariable Integer idFoto) {
        fotoService.eliminarFoto(idFoto);
        return ResponseEntity.noContent().build();
    }
}
