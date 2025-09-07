package com.deporuis.seleccion.infraestructura;

import com.deporuis.integrante.infraestructura.dto.IntegranteResponse;
import com.deporuis.logro.infraestructura.dto.LogroResponse;
import com.deporuis.publicacion.infraestructura.dto.PublicacionResponse;
import com.deporuis.seleccion.aplicacion.SeleccionService;
import com.deporuis.seleccion.infraestructura.dto.SeleccionRequest;
import com.deporuis.seleccion.infraestructura.dto.SeleccionResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/private/seleccion")
public class SeleccionController {

    @Autowired
    private SeleccionService seleccionService;

    @PostMapping("/crear")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ENTRENADOR')")
    public ResponseEntity<SeleccionResponse> crearSeleccion(@Valid @RequestBody SeleccionRequest seleccionRequest) {
        SeleccionResponse response = seleccionService.crearSeleccion(seleccionRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/lista")
    public ResponseEntity<Page<SeleccionResponse>> obtenerSeleccionesPaginadas(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "3") Integer size
    ) {
        Page<SeleccionResponse> pagina = seleccionService.obtenerSeleccionesPaginadas(page, size);
        return ResponseEntity.ok(pagina);
    }

    @GetMapping("/obtener/{id}")
    public ResponseEntity<SeleccionResponse> obtenerSeleccion(
            @PathVariable Integer id
    ) {
        SeleccionResponse seleccion = seleccionService.obtenerSeleccion(id);
        if (seleccion == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(seleccion);
    }

    @DeleteMapping("/eliminar/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> eliminarSeleccion(
            @PathVariable Integer id
    ) {
        seleccionService.eliminarSeleccion(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/softdelete/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ENTRENADOR')")
    public ResponseEntity<Void> softDeleteSeleccion(
            @PathVariable Integer id
    ) {
        seleccionService.softDeleteSeleccion(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/actualizar/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ENTRENADOR')")
    public ResponseEntity<SeleccionResponse> actualizarSeleccion(
            @PathVariable Integer id,
            @Valid @RequestBody SeleccionRequest seleccionRequest
    ){
        SeleccionResponse seleccionActualizada = seleccionService.actualizarSeleccion(id, seleccionRequest);
        return ResponseEntity.ok(seleccionActualizada);
    }

    @GetMapping("/{idSeleccion}/integrantes")
    public ResponseEntity<Page<IntegranteResponse>> obtenerIntegrantesDeSeleccion(
            @PathVariable Integer idSeleccion,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<IntegranteResponse> result =
                seleccionService.obtenerIntegrantesSeleccion(idSeleccion, page, size);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{idSeleccion}/logros")
    public ResponseEntity<Page<LogroResponse>> obtenerLogrosDeSeleccion(
            @PathVariable Integer idSeleccion,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "6") Integer size
    ) {
        var result = seleccionService.obtenerLogrosSeleccion(idSeleccion, page, size);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{idSeleccion}/eventos")
    public ResponseEntity<List<PublicacionResponse>> obtenerUltimosEventos(
            @PathVariable Integer idSeleccion
    ) {
        var body = seleccionService.ultimosEventos(idSeleccion);
        return ResponseEntity.ok(body);
    }
}
