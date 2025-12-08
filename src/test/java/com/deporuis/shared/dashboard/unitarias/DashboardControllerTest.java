package com.deporuis.shared.dashboard.unitarias;

import com.deporuis.auth.aplicacion.CustomUserDetailsService;
import com.deporuis.auth.aplicacion.JwtService;
import com.deporuis.shared.dashboard.aplicacion.DashboardService;
import com.deporuis.shared.dashboard.dominio.StatType;
import com.deporuis.shared.dashboard.infraestructura.DashboardController;
import com.deporuis.shared.dashboard.infraestructura.dto.DashboardStatResponse;
import com.deporuis.shared.dashboard.infraestructura.dto.TrendData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DashboardController.class)
@AutoConfigureMockMvc(addFilters = false)
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DashboardService dashboardService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @WithMockUser
    void obtenerEstadistica_SELE_deberiaRetornar200() throws Exception {
        // Arrange
        DashboardStatResponse response = new DashboardStatResponse(
                "Total selecciones",
                15L,
                new TrendData(20.0, true),
                true
        );
        when(dashboardService.obtenerEstadistica(StatType.SELE)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/public/dashboard/stats/SELE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Total selecciones"))
                .andExpect(jsonPath("$.value").value(15))
                .andExpect(jsonPath("$.trend.value").value(20.0))
                .andExpect(jsonPath("$.trend.isPositive").value(true))
                .andExpect(jsonPath("$.year").value(true));

        verify(dashboardService).obtenerEstadistica(StatType.SELE);
    }

    @Test
    @WithMockUser
    void obtenerEstadistica_EVEN_deberiaRetornar200() throws Exception {
        // Arrange
        DashboardStatResponse response = new DashboardStatResponse(
                "Total eventos",
                20L,
                new TrendData(60.0, true),
                false
        );
        when(dashboardService.obtenerEstadistica(StatType.EVEN)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/public/dashboard/stats/EVEN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Total eventos"))
                .andExpect(jsonPath("$.value").value(20))
                .andExpect(jsonPath("$.trend.value").value(60.0))
                .andExpect(jsonPath("$.trend.isPositive").value(true))
                .andExpect(jsonPath("$.year").value(false));
    }

    @Test
    @WithMockUser
    void obtenerEstadistica_NOTI_deberiaRetornar200() throws Exception {
        // Arrange
        DashboardStatResponse response = new DashboardStatResponse(
                "Total noticias",
                30L,
                new TrendData(40.0, false),
                false
        );
        when(dashboardService.obtenerEstadistica(StatType.NOTI)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/public/dashboard/stats/NOTI"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Total noticias"))
                .andExpect(jsonPath("$.value").value(30))
                .andExpect(jsonPath("$.trend.value").value(40.0))
                .andExpect(jsonPath("$.trend.isPositive").value(false))
                .andExpect(jsonPath("$.year").value(false));
    }

    @Test
    @WithMockUser
    void obtenerEstadistica_INTE_deberiaRetornar200() throws Exception {
        // Arrange
        DashboardStatResponse response = new DashboardStatResponse(
                "Total integrantes",
                50L,
                new TrendData(null, false),
                false
        );
        when(dashboardService.obtenerEstadistica(StatType.INTE)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/public/dashboard/stats/INTE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Total integrantes"))
                .andExpect(jsonPath("$.value").value(50))
                .andExpect(jsonPath("$.trend.value").isEmpty())
                .andExpect(jsonPath("$.trend.isPositive").value(false))
                .andExpect(jsonPath("$.year").value(false));
    }
}
