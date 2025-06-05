package com.deporuis.publicacion.infraestructura;

import com.deporuis.publicacion.aplicacion.PublicacionService;
import com.deporuis.publicacion.infraestructura.dto.PublicacionRequest;
import com.deporuis.publicacion.infraestructura.dto.PublicacionResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/publicacion")
@Controller
public class PublicacionController {

    @Autowired
    private PublicacionService publicacionService;

    @PostMapping
    public ResponseEntity<PublicacionResponse> crearPublicacion(@Valid @RequestBody PublicacionRequest publicacionRequest){
        PublicacionResponse publicacionCreada = publicacionService.crearPublicacion(publicacionRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(publicacionCreada);
    }
}
