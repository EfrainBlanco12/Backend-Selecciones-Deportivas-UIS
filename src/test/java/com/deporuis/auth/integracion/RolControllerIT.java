package com.deporuis.auth.integracion;

import com.deporuis.auth.aplicacion.CustomUserDetailsService;
import com.deporuis.auth.aplicacion.JwtService;
import com.deporuis.auth.aplicacion.RolService;
import com.deporuis.auth.infraestructura.RolController;
import com.deporuis.auth.infraestructura.dto.RolResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RolController.class)
class RolControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RolService rolService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @WithMockUser(roles = {"ADMINISTRADOR"})
    void obtenerRolesExceptoAdministrador_conRolAdministrador_deberiaRetornarListaRoles() throws Exception {
        // Arrange
        RolResponse rol1 = new RolResponse(2, "ENTRENADOR");
        RolResponse rol2 = new RolResponse(3, "DEPORTISTA");
        List<RolResponse> roles = Arrays.asList(rol1, rol2);

        when(rolService.obtenerRolesExceptoAdministrador()).thenReturn(roles);

        // Act & Assert
        mockMvc.perform(get("/private/rol/lista"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].idRol").value(2))
                .andExpect(jsonPath("$[0].nombreRol").value("ENTRENADOR"))
                .andExpect(jsonPath("$[1].idRol").value(3))
                .andExpect(jsonPath("$[1].nombreRol").value("DEPORTISTA"));

        verify(rolService).obtenerRolesExceptoAdministrador();
    }

    @Test
    @WithMockUser(roles = {"ENTRENADOR"})
    void obtenerRolesExceptoAdministrador_conRolEntrenador_deberiaRetornarListaRoles() throws Exception {
        // Arrange
        RolResponse rol1 = new RolResponse(2, "ENTRENADOR");
        RolResponse rol2 = new RolResponse(3, "DEPORTISTA");
        List<RolResponse> roles = Arrays.asList(rol1, rol2);

        when(rolService.obtenerRolesExceptoAdministrador()).thenReturn(roles);

        // Act & Assert
        mockMvc.perform(get("/private/rol/lista"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        verify(rolService).obtenerRolesExceptoAdministrador();
    }

    @Test
    @WithMockUser(roles = {"DEPORTISTA"})
    void obtenerRolesExceptoAdministrador_conRolDeportista_deberiaDenegarAcceso() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/private/rol/lista"))
                .andExpect(status().isForbidden());

        verify(rolService, never()).obtenerRolesExceptoAdministrador();
    }

    @Test
    void obtenerRolesExceptoAdministrador_sinAutenticacion_deberiaRetornarUnauthorized() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/private/rol/lista"))
                .andExpect(status().isUnauthorized());

        verify(rolService, never()).obtenerRolesExceptoAdministrador();
    }

    @Test
    @WithMockUser(roles = {"ADMINISTRADOR"})
    void obtenerRolesExceptoAdministrador_conListaVacia_deberiaRetornarArrayVacio() throws Exception {
        // Arrange
        when(rolService.obtenerRolesExceptoAdministrador()).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/private/rol/lista"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(rolService).obtenerRolesExceptoAdministrador();
    }

    @Test
    @WithMockUser(roles = {"ADMINISTRADOR"})
    void obtenerTodosLosRoles_conRolAdministrador_deberiaRetornarTodosLosRolesIncluyendoAdministrador() throws Exception {
        // Arrange
        RolResponse rol1 = new RolResponse(1, "ADMINISTRADOR");
        RolResponse rol2 = new RolResponse(2, "ENTRENADOR");
        RolResponse rol3 = new RolResponse(3, "DEPORTISTA");
        List<RolResponse> roles = Arrays.asList(rol1, rol2, rol3);

        when(rolService.obtenerTodosLosRoles()).thenReturn(roles);

        // Act & Assert
        mockMvc.perform(get("/private/rol/todos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].idRol").value(1))
                .andExpect(jsonPath("$[0].nombreRol").value("ADMINISTRADOR"))
                .andExpect(jsonPath("$[1].idRol").value(2))
                .andExpect(jsonPath("$[1].nombreRol").value("ENTRENADOR"))
                .andExpect(jsonPath("$[2].idRol").value(3))
                .andExpect(jsonPath("$[2].nombreRol").value("DEPORTISTA"));

        verify(rolService).obtenerTodosLosRoles();
    }

    @Test
    @WithMockUser(roles = {"ENTRENADOR"})
    void obtenerTodosLosRoles_conRolEntrenador_deberiaDenegarAcceso() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/private/rol/todos"))
                .andExpect(status().isForbidden());

        verify(rolService, never()).obtenerTodosLosRoles();
    }

    @Test
    @WithMockUser(roles = {"DEPORTISTA"})
    void obtenerTodosLosRoles_conRolDeportista_deberiaDenegarAcceso() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/private/rol/todos"))
                .andExpect(status().isForbidden());

        verify(rolService, never()).obtenerTodosLosRoles();
    }

    @Test
    void obtenerTodosLosRoles_sinAutenticacion_deberiaRetornarUnauthorized() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/private/rol/todos"))
                .andExpect(status().isUnauthorized());

        verify(rolService, never()).obtenerTodosLosRoles();
    }

    @Test
    @WithMockUser(roles = {"ADMINISTRADOR"})
    void obtenerTodosLosRoles_conListaVacia_deberiaRetornarArrayVacio() throws Exception {
        // Arrange
        when(rolService.obtenerTodosLosRoles()).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/private/rol/todos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(rolService).obtenerTodosLosRoles();
    }
}
