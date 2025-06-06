package com.deporuis.deporte.infraestructura;

import com.deporuis.deporte.aplicacion.DeporteService;
import com.deporuis.deporte.infraestructura.dto.DeporteRequest;
import com.deporuis.deporte.infraestructura.dto.DeporteResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/deporte")
@Controller
public class DeporteController {

    @Autowired
    private DeporteService deporteService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ENTRENADOR')")
    public ResponseEntity<DeporteResponse> crearDeporte(@Valid @RequestBody DeporteRequest deporteRequest){
        DeporteResponse deporteCreado = deporteService.crearDeporte(deporteRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(deporteCreado);
    };
}
