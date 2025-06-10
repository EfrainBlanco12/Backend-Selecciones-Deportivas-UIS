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

@RequestMapping("/publicacion")
@Controller
public class PublicacionController {

    @Autowired
    private PublicacionService publicacionService;

    /**
     * Crear publicacion (POST /publicacion)
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ENTRENADOR')")
    public ResponseEntity<PublicacionResponse> crearPublicacion(@Valid @RequestBody PublicacionRequest publicacionRequest){
        PublicacionResponse publicacionCreada = publicacionService.crearPublicacion(publicacionRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(publicacionCreada);
    }

    /**
     * Obtener publicaciones por paginas, definiendo el numero de la pagina y su tamaño (GET /publicacion)
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ENTRENADOR', 'DEPORTISTA')")
    public ResponseEntity<Page<PublicacionResponse>> obtenerPublicacionesPaginadas(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "2") Integer size
    ) {
        Page<PublicacionResponse> pagina = publicacionService.obtenerPublicacionesPaginadas(page, size);
        return ResponseEntity.ok(pagina);
    }

    /**
     * Obtener una sola publicación por su ID (GET /publicacion/{id})
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ENTRENADOR', 'DEPORTISTA')")
    public ResponseEntity<PublicacionResponse> obtenerPublicacion(
            @PathVariable Integer id
    ) {
        PublicacionResponse publicacion = publicacionService.obtenerPublicacion(id);
        if(publicacion == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(publicacion);
    }

    /**
     * Actualizar una publicación existente (PUT /publicacion/{id})
     */
    @PutMapping("/{id}")
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
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> eliminarPublicacion(
            @PathVariable Integer id
    ) {
        publicacionService.eliminarPublicacion(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Hace softdelete a una publicación por su ID (PATCH /publicacion/{id})
     */
    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ENTRENADOR')")
    public ResponseEntity<Void> softDeletePublicacion(
            @PathVariable Integer id
    ) {
        publicacionService.softDeletePublicacion(id);
        return ResponseEntity.noContent().build();
    }
}
