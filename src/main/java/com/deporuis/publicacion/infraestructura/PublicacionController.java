package com.deporuis.publicacion.infraestructura;

import com.deporuis.publicacion.aplicacion.PublicacionService;
import com.deporuis.publicacion.dominio.Publicacion;
import com.deporuis.publicacion.infraestructura.dto.PublicacionRequest;
import com.deporuis.publicacion.infraestructura.dto.PublicacionResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/publicacion")
@Controller
public class PublicacionController {

    @Autowired
    private PublicacionService publicacionService;

    @PostMapping
    public ResponseEntity<PublicacionResponse> crearPublicacion(@Valid @RequestBody PublicacionRequest publicacionRequest){
        PublicacionResponse publicacionCreada = publicacionService.crearPublicacion(publicacionRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(publicacionCreada);
    }

    @GetMapping
    public ResponseEntity<Page<PublicacionResponse>> obtenerPublicacionesPaginadas(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "2") int size
    ) {
        Page<PublicacionResponse> pagina = publicacionService.obtenerPublicacionesPaginadas(page, size);
        return ResponseEntity.ok(pagina);
    }

    /**
     * Obtener una sola publicación por su ID (GET /publicacion/{id})
     */
    @GetMapping("/{id}")
    public ResponseEntity<PublicacionResponse> obtenerPublicacion(
            @PathVariable int id
    ) {
        PublicacionResponse publicacion = publicacionService.obtenerPublicacion(id);
//        if(publicacion == null) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body();
//        }
        return ResponseEntity.ok(publicacion);
    }

    /**
     * Actualizar una publicación existente (PUT /publicacion/{id})
     */
    @PutMapping("/{id}")
    public ResponseEntity<PublicacionResponse> actualizarPublicacion(
            @PathVariable int id,
            @Valid @RequestBody PublicacionRequest publicacionRequest
    ) {
        PublicacionResponse publicacionActualizada = publicacionService.actualizarPublicacion(id, publicacionRequest);
        return ResponseEntity.ok(publicacionActualizada);
    }

    /**
     * Eliminar una publicación por su ID (DELETE /publicacion/{id})
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPublicacion(
            @PathVariable int id
    ) {
        publicacionService.eliminarPublicacion(id);
        return ResponseEntity.noContent().build();
    }
}
