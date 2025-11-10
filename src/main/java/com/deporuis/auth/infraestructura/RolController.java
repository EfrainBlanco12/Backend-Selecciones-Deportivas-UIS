package com.deporuis.auth.infraestructura;

import com.deporuis.auth.aplicacion.RolService;
import com.deporuis.auth.infraestructura.dto.RolResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("/private/rol")
@Controller
public class RolController {

    @Autowired
    private RolService rolService;

    /**
     * Obtener todos los roles excepto ADMINISTRADOR (GET /rol/lista)
     */
    @GetMapping("/lista")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ENTRENADOR')")
    public ResponseEntity<List<RolResponse>> obtenerRolesExceptoAdministrador() {
        List<RolResponse> roles = rolService.obtenerRolesExceptoAdministrador();
        return ResponseEntity.ok(roles);
    }

    /**
     * Obtener todos los roles incluyendo ADMINISTRADOR (GET /rol/todos)
     */
    @GetMapping("/todos")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<RolResponse>> obtenerTodosLosRoles() {
        List<RolResponse> roles = rolService.obtenerTodosLosRoles();
        return ResponseEntity.ok(roles);
    }
}
