package com.deporuis.integrante.infraestructura;

import com.deporuis.integrante.aplicacion.IntegranteService;
import com.deporuis.integrante.infraestructura.dto.IntegranteRequest;
import com.deporuis.integrante.infraestructura.dto.IntegranteResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
