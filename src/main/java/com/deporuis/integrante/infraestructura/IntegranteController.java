package com.deporuis.integrante.infraestructura;

import com.deporuis.integrante.aplicacion.IntegranteService;
import com.deporuis.integrante.infraestructura.dto.IntegranteRequest;
import com.deporuis.integrante.infraestructura.dto.IntegranteResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/private/integrante")
public class IntegranteController {

    @Autowired
    private IntegranteService integranteService;

    /**
     * Crear un integrante de seleccion (POST /integrante)
     */
    @PostMapping("/crear")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ENTRENADOR')")
    public ResponseEntity<IntegranteResponse> crearIntegrante(
            @Valid @RequestBody IntegranteRequest integranteRequest
    ) {
        IntegranteResponse integranteCreado = integranteService.crearIntegrante(integranteRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(integranteCreado);
    }

    /**
     * Obtener una lista paginada de integrantes, definiendo el numero de pagina
     * y su tamaño (GET /integrante)
     */
    @GetMapping("/lista")
    public ResponseEntity<Page<IntegranteResponse>> obtenerIntegrantesPaginados(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "6") Integer size
    ) {
        Page<IntegranteResponse> pagina = integranteService.obtenerIntegrantesPaginados(page, size);
        return ResponseEntity.ok(pagina);
    }

    /**
     * Obtener un integrante por su ID (GET /integrante/{id}
     */
    @GetMapping("/obtener/{id}")
    public ResponseEntity<IntegranteResponse> obtenerIntegrante(
            @PathVariable Integer id
    ) {
        IntegranteResponse integrante = integranteService.obtenerIntegrante(id);
        if (integrante == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(integrante);
    }

    /**
     * Hace softdelete de un integrante por su ID (PATCH /integrante)
     */
    @PatchMapping("/softdelete/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ENTRENADOR')")
    public ResponseEntity<Void> softDeleteIntegrante(
            @PathVariable Integer id
    ) {
        integranteService.softDeleteIntegrante(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Actualiza un integrante por su ID, cambiando su foto y relaciones correspondientes con horarios,
     * posiciones, rol y seleccion
     */
    @PutMapping("/actualizar/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ENTRENADOR')")
    public ResponseEntity<IntegranteResponse> actualizarIntegrante(
            @PathVariable Integer id,
            @Valid @RequestBody IntegranteRequest integranteRequest
    ) {
        IntegranteResponse integranteActualizado = integranteService.actualizarIntegrante(id, integranteRequest);
        return ResponseEntity.ok(integranteActualizado);
    }

    @GetMapping("/{idSeleccion}/entrenador")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','ENTRENADOR')")
    public ResponseEntity<IntegranteResponse> obtenerEntrenador(
            @PathVariable Integer idSeleccion
    ) {
        return ResponseEntity.ok(integranteService.obtenerEntrenadorPorSeleccion(idSeleccion));
    }

    @GetMapping("/{idSeleccion}/conteo")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','ENTRENADOR')")
    public ResponseEntity<Long> contarIntegrantes(
            @PathVariable Integer idSeleccion
    ) {
        return ResponseEntity.ok(integranteService.contarIntegrantesPorSeleccion(idSeleccion));
    }
}
