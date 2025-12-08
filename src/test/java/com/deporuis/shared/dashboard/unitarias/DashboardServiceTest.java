package com.deporuis.shared.dashboard.unitarias;

import com.deporuis.integrante.infraestructura.IntegranteRepository;
import com.deporuis.publicacion.dominio.TipoPublicacion;
import com.deporuis.publicacion.infraestructura.PublicacionRepository;
import com.deporuis.seleccion.infraestructura.SeleccionRepository;
import com.deporuis.shared.dashboard.aplicacion.DashboardService;
import com.deporuis.shared.dashboard.dominio.StatType;
import com.deporuis.shared.dashboard.infraestructura.dto.DashboardStatResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @InjectMocks
    private DashboardService dashboardService;

    @Mock
    private SeleccionRepository seleccionRepository;

    @Mock
    private PublicacionRepository publicacionRepository;

    @Mock
    private IntegranteRepository integranteRepository;

    @Test
    void obtenerEstadistica_SELE_deberiaRetornarEstadisticaDeSelecciones() {
        // Arrange
        when(seleccionRepository.countByVisibilidad(true)).thenReturn(15L); // Total hasta ahora
        when(seleccionRepository.countByFechaCreacionLessThanAndVisibilidad(
                any(LocalDate.class), eq(true)
        )).thenReturn(10L); // Antes del año actual

        // Act
        DashboardStatResponse result = dashboardService.obtenerEstadistica(StatType.SELE);

        // Assert
        assertNotNull(result);
        assertEquals("Total selecciones", result.getTitle());
        assertEquals(15L, result.getValue());
        assertNotNull(result.getTrend());
        assertEquals(50.0, result.getTrend().getValue()); // (15-10)/10 * 100 = 50%
        assertTrue(result.getTrend().getIsPositive());
        assertTrue(result.getYear());

        verify(seleccionRepository, times(2)).countByVisibilidad(true);
        verify(seleccionRepository).countByFechaCreacionLessThanAndVisibilidad(
                any(LocalDate.class), eq(true)
        );
    }

    @Test
    void obtenerEstadistica_SELE_sinDatosAnteriores_deberiaRetornar100PorCiento() {
        // Arrange
        when(seleccionRepository.countByVisibilidad(true)).thenReturn(5L); // Total hasta ahora
        when(seleccionRepository.countByFechaCreacionLessThanAndVisibilidad(
                any(LocalDate.class), eq(true)
        )).thenReturn(0L); // Antes del año actual = 0

        // Act
        DashboardStatResponse result = dashboardService.obtenerEstadistica(StatType.SELE);

        // Assert
        assertNotNull(result);
        assertEquals(100.0, result.getTrend().getValue());
        assertTrue(result.getTrend().getIsPositive());
        assertTrue(result.getYear());
    }

    @Test
    void obtenerEstadistica_EVEN_deberiaRetornarEstadisticaDeEventos() {
        // Arrange
        when(publicacionRepository.countByTipoPublicacionAndVisibilidad(
                eq(TipoPublicacion.EVENTO), eq(true)
        )).thenReturn(20L); // Total hasta ahora
        when(publicacionRepository.countByTipoPublicacionAndFechaLessThanAndVisibilidad(
                eq(TipoPublicacion.EVENTO), any(LocalDateTime.class), eq(true)
        )).thenReturn(15L); // Antes del mes actual

        // Act
        DashboardStatResponse result = dashboardService.obtenerEstadistica(StatType.EVEN);

        // Assert
        assertNotNull(result);
        assertEquals("Total eventos", result.getTitle());
        assertEquals(20L, result.getValue());
        assertNotNull(result.getTrend());
        assertEquals(33.33, result.getTrend().getValue(), 0.01); // (20-15)/15 * 100 = 33.33%
        assertTrue(result.getTrend().getIsPositive());
        assertFalse(result.getYear());

        verify(publicacionRepository, times(2)).countByTipoPublicacionAndVisibilidad(
                eq(TipoPublicacion.EVENTO), eq(true)
        );
        verify(publicacionRepository).countByTipoPublicacionAndFechaLessThanAndVisibilidad(
                eq(TipoPublicacion.EVENTO), any(LocalDateTime.class), eq(true)
        );
    }

    @Test
    void obtenerEstadistica_NOTI_deberiaRetornarEstadisticaDeNoticias() {
        // Arrange
        when(publicacionRepository.countByTipoPublicacionAndVisibilidad(
                TipoPublicacion.NOTICIA, true
        )).thenReturn(30L);
        when(publicacionRepository.countByTipoPublicacionAndFechaLessThanAndVisibilidad(
                eq(TipoPublicacion.NOTICIA), any(LocalDateTime.class), eq(true)
        )).thenReturn(40L); // Antes del mes actual

        // Act
        DashboardStatResponse result = dashboardService.obtenerEstadistica(StatType.NOTI);

        // Assert
        assertNotNull(result);
        assertEquals("Total noticias", result.getTitle());
        assertEquals(30L, result.getValue());
        assertNotNull(result.getTrend());
        assertEquals(25.0, result.getTrend().getValue()); // (30-40)/40 * 100 = -25%, valor absoluto = 25
        assertFalse(result.getTrend().getIsPositive()); // Es negativo
        assertFalse(result.getYear());
    }

    @Test
    void obtenerEstadistica_INTE_deberiaRetornarEstadisticaDeIntegrantes() {
        // Arrange
        when(integranteRepository.countByVisibilidad(true)).thenReturn(50L);

        // Act
        DashboardStatResponse result = dashboardService.obtenerEstadistica(StatType.INTE);

        // Assert
        assertNotNull(result);
        assertEquals("Total integrantes", result.getTitle());
        assertEquals(50L, result.getValue());
        assertNotNull(result.getTrend());
        // Como no hay forma de comparar datos históricos, trend.value debe ser null
        assertNull(result.getTrend().getValue());
        assertFalse(result.getTrend().getIsPositive());
        assertFalse(result.getYear());

        verify(integranteRepository).countByVisibilidad(true);
    }

    @Test
    void obtenerEstadistica_conTendenciaNegativa_deberiaRetornarValorAbsoluto() {
        // Arrange - simular decrecimiento
        when(publicacionRepository.countByTipoPublicacionAndVisibilidad(
                TipoPublicacion.EVENTO, true
        )).thenReturn(8L); // Total hasta ahora
        when(publicacionRepository.countByTipoPublicacionAndFechaLessThanAndVisibilidad(
                eq(TipoPublicacion.EVENTO), any(LocalDateTime.class), eq(true)
        )).thenReturn(12L); // Antes del mes actual (más que ahora = decrecimiento)

        // Act
        DashboardStatResponse result = dashboardService.obtenerEstadistica(StatType.EVEN);

        // Assert
        assertNotNull(result.getTrend());
        assertEquals(33.33, result.getTrend().getValue(), 0.01); // |(8-12)/12 * 100| = 33.33%
        assertFalse(result.getTrend().getIsPositive());
        assertFalse(result.getYear());
    }
}
