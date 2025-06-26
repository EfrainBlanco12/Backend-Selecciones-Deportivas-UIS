package com.deporuis.seleccion.infraestructura;

import com.deporuis.seleccion.aplicacion.SeleccionService;
import com.deporuis.seleccion.infraestructura.dto.SeleccionRequest;
import com.deporuis.seleccion.infraestructura.dto.SeleccionResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/private/seleccion")  // más semántico en plural
public class SeleccionController {

    @Autowired
    private SeleccionService seleccionService;

    /**
     * Crear una seleccion (POST /seleccion)
     */
    @PostMapping("/crear")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ENTRENADOR')")
    public ResponseEntity<SeleccionResponse> crearSeleccion(@Valid @RequestBody SeleccionRequest seleccionRequest) {
        SeleccionResponse response = seleccionService.crearSeleccion(seleccionRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Obtener selecciones por paginas, definiendo el numero de la pagina y su tamaño (GET /seleccion)
     */
    @GetMapping("/lista")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ENTRENADOR', 'DEPORTISTA')")
    public ResponseEntity<Page<SeleccionResponse>> obtenerSeleccionesPaginadas(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "3") Integer size
    ) {
        Page<SeleccionResponse> pagina = seleccionService.obtenerSeleccionesPaginadas(page, size);
        return ResponseEntity.ok(pagina);
    }

    /**
     * Obtener una seleccion por su ID (GET /seleccion/{id}
     */
    @GetMapping("/obtener/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ENTRENADOR', 'DEPORTISTA')")
    public ResponseEntity<SeleccionResponse> obtenerSeleccion(
            @PathVariable Integer id
    ) {
        SeleccionResponse seleccion = seleccionService.obtenerSeleccion(id);
        if (seleccion == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(seleccion);
    }

//    @DeleteMapping("/eliminar/{id}")
//    @PreAuthorize("hasRole('ADMINISTRADOR')")
//
//    @PatchMapping("/softdelete/{id}")
//    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ENTRENADOR')")
//
//    @PutMapping("/actualizar/{id}")
//    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ENTRENADOR')")
}
