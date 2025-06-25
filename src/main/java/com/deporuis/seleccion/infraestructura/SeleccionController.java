package com.deporuis.seleccion.infraestructura;

import com.deporuis.seleccion.aplicacion.SeleccionService;
import com.deporuis.seleccion.infraestructura.dto.SeleccionRequest;
import com.deporuis.seleccion.infraestructura.dto.SeleccionResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/private/seleccion")  // más semántico en plural
public class SeleccionController {

    @Autowired
    private SeleccionService seleccionService;

    @PostMapping("/crear")
    public ResponseEntity<SeleccionResponse> crearSeleccion(@Valid @RequestBody SeleccionRequest seleccionRequest) {
        SeleccionResponse response = seleccionService.crearSeleccion(seleccionRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
