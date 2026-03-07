package com.deporuis.logro.infraestructura;

import com.deporuis.logro.aplicacion.LogroService;
import com.deporuis.logro.infraestructura.dto.LogroRequest;
import com.deporuis.logro.infraestructura.dto.LogroResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/private/logro")
@Controller
public class LogroController {

    @Autowired
    private LogroService logroService;

    /**
     *  Crear logro (POST /logro)
     */
    @PostMapping("/crear")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ENTRENADOR')")
    public ResponseEntity<LogroResponse> crearLogro(@Valid @RequestBody LogroRequest logroRequest) {
        LogroResponse logroCreado = logroService.crearLogro(logroRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(logroCreado);
    }

    /**
     *  Obtener lista paginada de logros (GET /logro)
     */
    @GetMapping("/lista")
    public ResponseEntity<Page<LogroResponse>> obtenerLogrosPaginados(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "4") Integer size
    ) {
        Page<LogroResponse> pagina = logroService.obtenerLogrosPaginados(page, size);
        return ResponseEntity.ok(pagina);
    }

    /**
     * Obtener un solo logro por su ID (GET /logro/{id})
     */
    @GetMapping("/obtener/{id}")
    public ResponseEntity<LogroResponse> obtenerLogro(
            @PathVariable Integer id
    ) {
        LogroResponse logro = logroService.obtenerLogro(id);
        if(logro == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(logro);
    }

    /**
     * Actualizar un logro existente (PUT /logro/{id})
     */
    @PutMapping("/actualizar/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ENTRENADOR')")
    public ResponseEntity<LogroResponse> actualizarLogro(
            @PathVariable Integer id,
            @Valid @RequestBody LogroRequest logroRequest
    ) {
        LogroResponse logroActualizado = logroService.actualizarLogro(id, logroRequest);
        return ResponseEntity.ok(logroActualizado);
    }

    /**
     * Eliminar un logro  por su ID (DELETE /logro/{id})
     */
    @DeleteMapping("/eliminar/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ENTRENADOR')")
    public ResponseEntity<Void> eliminarLogro(
            @PathVariable Integer id
    ) {
        logroService.eliminarLogro(id);
        return ResponseEntity.noContent().build();
    }
}
