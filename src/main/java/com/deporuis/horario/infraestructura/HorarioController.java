package com.deporuis.horario.infraestructura;

import com.deporuis.horario.aplicacion.HorarioService;
import com.deporuis.horario.infraestructura.dto.HorarioRequest;
import com.deporuis.horario.infraestructura.dto.HorarioResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/private/horario")
@Controller
public class HorarioController {

    @Autowired
    private HorarioService horarioService;

    /**
     * Crear Horarios (POST /horario)
     */
    @PostMapping("/crear")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ENTRENADOR')")
    public ResponseEntity<HorarioResponse> crearHorario(@Valid @RequestBody HorarioRequest horarioRequest) {
        HorarioResponse horarioCreado = horarioService.crearHorario(horarioRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(horarioCreado);
    }

    /**
     * Obtener un horario por su ID (GET /horario/{id})
     */
    @GetMapping("/obtener/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ENTRENADOR', 'DEPORTISTA')")
    public ResponseEntity<HorarioResponse> obtenerHorario(
            @PathVariable Integer id
    ) {
        HorarioResponse horario = horarioService.obtenerHorario(id);
        return ResponseEntity.ok(horario);
    }

    /**
     * Obtiene una lista paginada de horarios, definiendo el numero de pagina y su tamaño (GET /horario)
     */
    @GetMapping("/lista")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ENTRENADOR', 'DEPORTISTA')")
    public ResponseEntity<Page<HorarioResponse>> obtenerHorariosPaginados(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "5") Integer size
    ) {
        Page<HorarioResponse> pagina = horarioService.obtenerHorariosPaginados(page, size);
        return ResponseEntity.ok(pagina);
    }
}
