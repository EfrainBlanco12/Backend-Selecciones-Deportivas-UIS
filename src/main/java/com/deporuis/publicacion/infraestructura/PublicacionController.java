package com.deporuis.publicacion.infraestructura;

import com.deporuis.publicacion.aplicacion.PublicacionService;
import com.deporuis.publicacion.infraestructura.dto.PublicacionRequest;
import com.deporuis.publicacion.infraestructura.dto.PublicacionResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/private/publicacion")
@Controller
public class PublicacionController {

    @Autowired
    private PublicacionService publicacionService;

    /**
     * Crear publicacion (POST /publicacion)
     */
    @PostMapping("/crear")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ENTRENADOR')")
    public ResponseEntity<PublicacionResponse> crearPublicacion(@Valid @RequestBody PublicacionRequest publicacionRequest){
        PublicacionResponse publicacionCreada = publicacionService.crearPublicacion(publicacionRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(publicacionCreada);
    }
    @GetMapping("/{idPublicacion}")
    public ResponseEntity<PublicacionResponse> obtenerEventoPorId(
            @PathVariable Integer idPublicacion) {
        var resp = publicacionService.obtenerPublicacion(idPublicacion);
        return ResponseEntity.ok(resp);
    }

    /**
     * Obtener publicaciones por paginas, definiendo el numero de la pagina y su tamaño (GET /publicacion)
     */
    @GetMapping("/lista")
    public ResponseEntity<Page<PublicacionResponse>> obtenerPublicacionesPaginadas(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "5") Integer size
    ) {
        Page<PublicacionResponse> pagina = publicacionService.obtenerPublicacionesPaginadas(page, size);
        return ResponseEntity.ok(pagina);
    }

    /**
     * Obtener publicaciones de tipo NOTICIA (GET /private/publicacion/lista/noticias)
     */
    @GetMapping("/lista/noticias")
    public ResponseEntity<Page<PublicacionResponse>> obtenerNoticiasPaginadas(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "5") Integer size
    ) {
        Page<PublicacionResponse> pagina = publicacionService.obtenerNoticiasPaginadas(page, size);
        return ResponseEntity.ok(pagina);
    }

    /**
     * Obtener publicaciones de tipo EVENTO (GET /private/publicacion/lista/evento)
     */
    @GetMapping("/lista/eventos")
    public ResponseEntity<Page<PublicacionResponse>> obtenerEventosPaginados(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "5") Integer size
    ) {
        Page<PublicacionResponse> pagina = publicacionService.obtenerEventosPaginados(page, size);
        return ResponseEntity.ok(pagina);
    }

    /**
     * Obtener una sola publicación por su ID (GET /publicacion/{id})
     */



    /**
     * Actualizar una publicación existente (PUT /publicacion/{id})
     */
    @PutMapping("/actualizar/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ENTRENADOR')")
    public ResponseEntity<PublicacionResponse> actualizarPublicacion(
            @PathVariable Integer id,
            @Valid @RequestBody PublicacionRequest publicacionRequest
    ) {
        PublicacionResponse publicacionActualizada = publicacionService.actualizarPublicacion(id, publicacionRequest);
        return ResponseEntity.ok(publicacionActualizada);
    }

    /**
     * Eliminar una publicación por su ID (DELETE /publicacion/{id})
     */
    @DeleteMapping("/eliminar/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> eliminarPublicacion(
            @PathVariable Integer id
    ) {
        publicacionService.eliminarPublicacion(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Softdelete a una publicación por su ID (PATCH /publicacion/{id})
     */
    @PatchMapping("/softdelete/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ENTRENADOR')")
    public ResponseEntity<Void> softDeletePublicacion(
            @PathVariable Integer id
    ) {
        publicacionService.softDeletePublicacion(id);
        return ResponseEntity.noContent().build();
    }
}
