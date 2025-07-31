package com.deporuis.deporte.infraestructura;

import com.deporuis.deporte.aplicacion.DeporteQueryService;
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

@RequestMapping("/private/deporte")
@Controller
public class DeporteController {

    @Autowired
    private DeporteService deporteService;

    @Autowired
    private DeporteQueryService deporteQueryService;

    @PostMapping("/crear")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ENTRENADOR')")
    public ResponseEntity<DeporteResponse> crearDeporte(@Valid @RequestBody DeporteRequest deporteRequest){
        DeporteResponse deporteCreado = deporteService.crearDeporte(deporteRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(deporteCreado);
    };

    @GetMapping("/lista")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ENTRENADOR')")
    public ResponseEntity<List<DeporteResponse>> obtenerTodosLosDeportesVisibles(){
        List<DeporteResponse> deporteResponse = deporteService.obtenerTodosLosDeportesVisibles();
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

    @PutMapping("/softdelete/{id_deporte}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<DeporteResponse> softDeleteDeporte(@PathVariable("id_deporte") Integer idDeporte){
        DeporteResponse deporteEliminado = deporteService.softDeleteDeporte(idDeporte);
        return ResponseEntity.status(HttpStatus.OK).body(deporteEliminado);
    }
}
