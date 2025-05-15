package com.deporuis.seleccion.infraestructura;

import com.deporuis.seleccion.aplicacion.SeleccionDto;
import com.deporuis.seleccion.aplicacion.SeleccionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/seleccion")
@Controller
public class SeleccionController {

    @Autowired
    private SeleccionService seleccionService;

    @PostMapping
    public ResponseEntity<Void> crearSeleccion(@Valid @RequestBody SeleccionDto seleccionDto) {
        seleccionService.crearSeleccion(seleccionDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
