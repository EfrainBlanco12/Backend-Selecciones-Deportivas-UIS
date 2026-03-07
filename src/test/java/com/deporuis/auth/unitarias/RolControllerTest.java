package com.deporuis.auth.unitarias;

import com.deporuis.auth.aplicacion.RolService;
import com.deporuis.auth.infraestructura.RolController;
import com.deporuis.auth.infraestructura.dto.RolResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RolControllerTest {

    @Mock
    private RolService rolService;

    @InjectMocks
    private RolController controller;

    @Test
    void obtenerRolesExceptoAdministrador_debeRetornarListaDeRoles() {
        List<RolResponse> roles = List.of(
                new RolResponse(2, "ENTRENADOR"),
                new RolResponse(3, "DEPORTISTA")
        );
        when(rolService.obtenerRolesExceptoAdministrador()).thenReturn(roles);

        ResponseEntity<List<RolResponse>> result = controller.obtenerRolesExceptoAdministrador();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(2, result.getBody().size());
        assertFalse(result.getBody().stream().anyMatch(r -> r.getNombreRol().equals("ADMINISTRADOR")));
        verify(rolService).obtenerRolesExceptoAdministrador();
    }

    @Test
    void obtenerTodosLosRoles_debeRetornarTodosLosRoles() {
        List<RolResponse> roles = List.of(
                new RolResponse(1, "ADMINISTRADOR"),
                new RolResponse(2, "ENTRENADOR"),
                new RolResponse(3, "DEPORTISTA")
        );
        when(rolService.obtenerTodosLosRoles()).thenReturn(roles);

        ResponseEntity<List<RolResponse>> result = controller.obtenerTodosLosRoles();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(3, result.getBody().size());
        assertTrue(result.getBody().stream().anyMatch(r -> r.getNombreRol().equals("ADMINISTRADOR")));
        verify(rolService).obtenerTodosLosRoles();
    }
}
