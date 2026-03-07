package com.deporuis.shared.dashboard.integracion;

import com.deporuis.shared.dashboard.aplicacion.DashboardService;
import com.deporuis.shared.dashboard.dominio.StatType;
import com.deporuis.shared.dashboard.infraestructura.DashboardController;
import com.deporuis.shared.dashboard.infraestructura.dto.DashboardStatResponse;
import com.deporuis.shared.dashboard.infraestructura.dto.TrendData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@TestPropertySource(properties = "spring.profiles.active=test")
@WebMvcTest(controllers = DashboardController.class)
@AutoConfigureMockMvc(addFilters = false)
class DashboardControllerIT {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private DashboardService dashboardService;

    @MockitoBean
    private com.deporuis.auth.infraestructura.JwtFilter jwtFilter;

    @Test
    @DisplayName("GET /public/dashboard/stats/SELE -> 200 con estadísticas de selecciones")
    @WithMockUser(roles = {"ENTRENADOR"})
    void obtenerEstadisticaSelecciones_deberiaRetornar200() throws Exception {
        // Arrange
        TrendData trend = new TrendData(50.0, true);
        DashboardStatResponse response = new DashboardStatResponse(
                "Total selecciones",
                15L,
                trend,
                true
        );

        when(dashboardService.obtenerEstadistica(StatType.SELE)).thenReturn(response);

        // Act & Assert
        mvc.perform(get("/public/dashboard/stats/SELE")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("Total selecciones"))
                .andExpect(jsonPath("$.value").value(15))
                .andExpect(jsonPath("$.trend.value").value(50.0))
                .andExpect(jsonPath("$.trend.isPositive").value(true))
                .andExpect(jsonPath("$.year").value(true));
    }

    @Test
    @DisplayName("GET /public/dashboard/stats/EVEN -> 200 con estadísticas de eventos")
    @WithMockUser(roles = {"ENTRENADOR"})
    void obtenerEstadisticaEventos_deberiaRetornar200() throws Exception {
        // Arrange
        TrendData trend = new TrendData(33.33, true);
        DashboardStatResponse response = new DashboardStatResponse(
                "Total eventos",
                20L,
                trend,
                false
        );

        when(dashboardService.obtenerEstadistica(StatType.EVEN)).thenReturn(response);

        // Act & Assert
        mvc.perform(get("/public/dashboard/stats/EVEN")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("Total eventos"))
                .andExpect(jsonPath("$.value").value(20))
                .andExpect(jsonPath("$.trend.value").value(33.33))
                .andExpect(jsonPath("$.trend.isPositive").value(true))
                .andExpect(jsonPath("$.year").value(false));
    }

    @Test
    @DisplayName("GET /public/dashboard/stats/NOTI -> 200 con estadísticas de noticias")
    @WithMockUser(roles = {"DEPORTISTA"})
    void obtenerEstadisticaNoticias_deberiaRetornar200() throws Exception {
        // Arrange
        TrendData trend = new TrendData(25.0, false);
        DashboardStatResponse response = new DashboardStatResponse(
                "Total noticias",
                30L,
                trend,
                false
        );

        when(dashboardService.obtenerEstadistica(StatType.NOTI)).thenReturn(response);

        // Act & Assert
        mvc.perform(get("/public/dashboard/stats/NOTI")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("Total noticias"))
                .andExpect(jsonPath("$.value").value(30))
                .andExpect(jsonPath("$.trend.value").value(25.0))
                .andExpect(jsonPath("$.trend.isPositive").value(false))
                .andExpect(jsonPath("$.year").value(false));
    }

    @Test
    @DisplayName("GET /public/dashboard/stats/INTE -> 200 con estadísticas de integrantes")
    @WithMockUser(roles = {"ADMINISTRADOR"})
    void obtenerEstadisticaIntegrantes_deberiaRetornar200() throws Exception {
        // Arrange
        TrendData trend = new TrendData(null, false);
        DashboardStatResponse response = new DashboardStatResponse(
                "Total integrantes",
                50L,
                trend,
                false
        );

        when(dashboardService.obtenerEstadistica(StatType.INTE)).thenReturn(response);

        // Act & Assert
        mvc.perform(get("/public/dashboard/stats/INTE")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("Total integrantes"))
                .andExpect(jsonPath("$.value").value(50))
                .andExpect(jsonPath("$.trend.value").isEmpty())
                .andExpect(jsonPath("$.trend.isPositive").value(false))
                .andExpect(jsonPath("$.year").value(false));
    }

    @Test
    @DisplayName("GET /public/dashboard/stats/{tipo} con tipo inválido -> 400")
    @WithMockUser(roles = {"ENTRENADOR"})
    void obtenerEstadistica_tipoInvalido_deberiaRetornar400() throws Exception {
        // Act & Assert
        mvc.perform(get("/public/dashboard/stats/INVALID")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    // NOTA: Test de autorización sin autenticación no aplica en @WebMvcTest con addFilters=false
    // La validación de @PreAuthorize("isAuthenticated()") se valida en tests de integración completos con @SpringBootTest
}
