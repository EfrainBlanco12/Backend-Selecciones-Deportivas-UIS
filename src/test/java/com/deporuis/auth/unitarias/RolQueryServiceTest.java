package com.deporuis.auth.unitarias;

import com.deporuis.auth.aplicacion.RolQueryService;
import com.deporuis.auth.aplicacion.mapper.RolMapper;
import com.deporuis.auth.dominio.Rol;
import com.deporuis.auth.infraestructura.RolRepository;
import com.deporuis.auth.infraestructura.dto.RolResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RolQueryServiceTest {

    @Mock
    private RolRepository rolRepository;

    @InjectMocks
    private RolQueryService rolQueryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void obtenerRolesExceptoAdministrador_deberiaRetornarListaDosRoles() {
        // Arrange
        Rol rolEntrenador = new Rol(2, "ENTRENADOR");
        Rol rolDeportista = new Rol(3, "DEPORTISTA");
        List<Rol> roles = Arrays.asList(rolEntrenador, rolDeportista);

        RolResponse responseEntrenador = new RolResponse(2, "ENTRENADOR");
        RolResponse responseDeportista = new RolResponse(3, "DEPORTISTA");

        when(rolRepository.findAllExceptAdministrador()).thenReturn(roles);

        try (MockedStatic<RolMapper> mockedMapper = mockStatic(RolMapper.class)) {
            mockedMapper.when(() -> RolMapper.toResponse(rolEntrenador)).thenReturn(responseEntrenador);
            mockedMapper.when(() -> RolMapper.toResponse(rolDeportista)).thenReturn(responseDeportista);

            // Act
            List<RolResponse> resultado = rolQueryService.obtenerRolesExceptoAdministrador();

            // Assert
            assertNotNull(resultado);
            assertEquals(2, resultado.size());
            assertTrue(resultado.stream().anyMatch(r -> r.getNombreRol().equals("ENTRENADOR")));
            assertTrue(resultado.stream().anyMatch(r -> r.getNombreRol().equals("DEPORTISTA")));
            assertFalse(resultado.stream().anyMatch(r -> r.getNombreRol().equals("ADMINISTRADOR")));
            verify(rolRepository).findAllExceptAdministrador();
        }
    }

    @Test
    void obtenerRolesExceptoAdministrador_deberiaRetornarListaVaciaSiNoHayRoles() {
        // Arrange
        when(rolRepository.findAllExceptAdministrador()).thenReturn(List.of());

        // Act
        List<RolResponse> resultado = rolQueryService.obtenerRolesExceptoAdministrador();

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(rolRepository).findAllExceptAdministrador();
    }

    @Test
    void obtenerTodosLosRoles_deberiaRetornarTodosLosRolesIncluyendoAdministrador() {
        // Arrange
        Rol rolAdministrador = new Rol(1, "ADMINISTRADOR");
        Rol rolEntrenador = new Rol(2, "ENTRENADOR");
        Rol rolDeportista = new Rol(3, "DEPORTISTA");
        List<Rol> roles = Arrays.asList(rolAdministrador, rolEntrenador, rolDeportista);

        RolResponse responseAdministrador = new RolResponse(1, "ADMINISTRADOR");
        RolResponse responseEntrenador = new RolResponse(2, "ENTRENADOR");
        RolResponse responseDeportista = new RolResponse(3, "DEPORTISTA");

        when(rolRepository.findAll()).thenReturn(roles);

        try (MockedStatic<RolMapper> mockedMapper = mockStatic(RolMapper.class)) {
            mockedMapper.when(() -> RolMapper.toResponse(rolAdministrador)).thenReturn(responseAdministrador);
            mockedMapper.when(() -> RolMapper.toResponse(rolEntrenador)).thenReturn(responseEntrenador);
            mockedMapper.when(() -> RolMapper.toResponse(rolDeportista)).thenReturn(responseDeportista);

            // Act
            List<RolResponse> resultado = rolQueryService.obtenerTodosLosRoles();

            // Assert
            assertNotNull(resultado);
            assertEquals(3, resultado.size());
            assertTrue(resultado.stream().anyMatch(r -> r.getNombreRol().equals("ADMINISTRADOR")));
            assertTrue(resultado.stream().anyMatch(r -> r.getNombreRol().equals("ENTRENADOR")));
            assertTrue(resultado.stream().anyMatch(r -> r.getNombreRol().equals("DEPORTISTA")));
            verify(rolRepository).findAll();
        }
    }

    @Test
    void obtenerTodosLosRoles_deberiaRetornarListaVaciaSiNoHayRoles() {
        // Arrange
        when(rolRepository.findAll()).thenReturn(List.of());

        // Act
        List<RolResponse> resultado = rolQueryService.obtenerTodosLosRoles();

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(rolRepository).findAll();
    }
}
