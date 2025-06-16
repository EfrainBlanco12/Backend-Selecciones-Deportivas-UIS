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
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ENTRENADOR')")
    public ResponseEntity<List<DeporteResponse>> obtenerTodosLosDeportes(){
        List<DeporteResponse> deporteResponse = deporteService.obtenerTodosLosDeportes();
        return ResponseEntity.status(HttpStatus.OK).body(deporteResponse);
    }

    @PutMapping("/actualizar/{id_deporte}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ENTRENADOR')")
    public ResponseEntity<DeporteResponse> actualizarDeporte(
            @PathVariable("id_deporte") Integer idDeporte,
            @Valid @RequestBody DeporteRequest deporteRequest) {

        DeporteResponse deporteActualizado = deporteService.actualizarDeporte(idDeporte, deporteRequest);
        return ResponseEntity.status(HttpStatus.OK).body(deporteActualizado);
    }

}
