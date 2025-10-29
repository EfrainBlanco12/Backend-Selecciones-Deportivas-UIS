package com.deporuis.auth.unitarias;

import com.deporuis.auth.aplicacion.RolQueryService;
import com.deporuis.auth.aplicacion.RolService;
import com.deporuis.auth.infraestructura.dto.RolResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RolServiceTest {

    @Mock
    private RolQueryService rolQueryService;

    @InjectMocks
    private RolService rolService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void obtenerRolesExceptoAdministrador_deberiaDelegarEnRolQueryService() {
        // Arrange
        RolResponse response1 = new RolResponse(2, "ENTRENADOR");
        RolResponse response2 = new RolResponse(3, "DEPORTISTA");
        List<RolResponse> rolesEsperados = Arrays.asList(response1, response2);

        when(rolQueryService.obtenerRolesExceptoAdministrador()).thenReturn(rolesEsperados);

        // Act
        List<RolResponse> resultado = rolService.obtenerRolesExceptoAdministrador();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals(rolesEsperados, resultado);
        verify(rolQueryService).obtenerRolesExceptoAdministrador();
    }
}
