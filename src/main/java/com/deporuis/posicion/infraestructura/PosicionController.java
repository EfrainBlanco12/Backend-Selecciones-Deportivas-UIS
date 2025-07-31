package com.deporuis.posicion.infraestructura;

import com.deporuis.posicion.aplicacion.PosicionService;
import com.deporuis.posicion.infraestructura.dto.PosicionActualizarRequest;
import com.deporuis.posicion.infraestructura.dto.PosicionRequest;
import com.deporuis.posicion.infraestructura.dto.PosicionResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/private/posicion")
public class PosicionController {

    @Autowired
    private PosicionService service;

    @PostMapping("/crear")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ENTRENADOR')")
    public ResponseEntity<PosicionResponse> crearPosicion(@Valid @RequestBody PosicionRequest posicionRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crearPosicion(posicionRequest));
    }

    @GetMapping("/lista/{idDeporte}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ENTRENADOR')")
    public ResponseEntity<List<PosicionResponse>> listarPosicionPorDeporte(@PathVariable Integer idDeporte) {
        return ResponseEntity.ok(service.obtenerPosicionPorDeporte(idDeporte));
    }

    @PutMapping("/actualizar/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ENTRENADOR')")
    public ResponseEntity<PosicionResponse> actualizarPosicion(
            @PathVariable Integer id,
            @Valid @RequestBody PosicionActualizarRequest request) {
        return ResponseEntity.ok(service.actualizarPosicion(id, request));
    }

    @PutMapping("/softdelete/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ENTRENADOR')")
    public ResponseEntity<PosicionResponse> eliminar(@PathVariable Integer id) {
        return ResponseEntity.ok(service.softDelete(id));
    }
}
