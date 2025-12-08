package com.deporuis.shared.dashboard.aplicacion;

import com.deporuis.integrante.infraestructura.IntegranteRepository;
import com.deporuis.publicacion.dominio.TipoPublicacion;
import com.deporuis.publicacion.infraestructura.PublicacionRepository;
import com.deporuis.seleccion.infraestructura.SeleccionRepository;
import com.deporuis.shared.dashboard.dominio.StatType;
import com.deporuis.shared.dashboard.infraestructura.dto.DashboardStatResponse;
import com.deporuis.shared.dashboard.infraestructura.dto.TrendData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final SeleccionRepository seleccionRepository;
    private final PublicacionRepository publicacionRepository;
    private final IntegranteRepository integranteRepository;

    public DashboardStatResponse obtenerEstadistica(StatType tipo) {
        return switch (tipo) {
            case SELE -> obtenerEstadisticaSelecciones();
            case EVEN -> obtenerEstadisticaEventos();
            case NOTI -> obtenerEstadisticaNoticias();
            case INTE -> obtenerEstadisticaIntegrantes();
        };
    }

    private DashboardStatResponse obtenerEstadisticaSelecciones() {
        // Total de selecciones visibles actuales
        Long totalActual = seleccionRepository.countByVisibilidad(true);

        // Selecciones creadas antes del año actual
        int anioActual = LocalDate.now().getYear();
        LocalDate inicioAnioActual = LocalDate.of(anioActual, 1, 1);
        Long seleccionesAntesAnioActual = seleccionRepository.countByFechaCreacionLessThanAndVisibilidad(
                inicioAnioActual, true
        );

        // Selecciones hasta ahora (antes del año actual + año actual)
        Long seleccionesHastaAhora = seleccionRepository.countByVisibilidad(true);

        // Calcular porcentaje de cambio
        TrendData trend = calcularTendencia(seleccionesHastaAhora, seleccionesAntesAnioActual);

        return new DashboardStatResponse(
                "Total selecciones",
                totalActual,
                trend,
                true
        );
    }

    private DashboardStatResponse obtenerEstadisticaEventos() {
        // Total de eventos visibles
        Long totalActual = publicacionRepository.countByTipoPublicacionAndVisibilidad(
                TipoPublicacion.EVENTO, true
        );

        // Eventos con fecha antes del mes actual
        LocalDateTime inicioMesActual = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        Long eventosAntesMesActual = publicacionRepository.countByTipoPublicacionAndFechaLessThanAndVisibilidad(
                TipoPublicacion.EVENTO, inicioMesActual, true
        );

        // Eventos hasta ahora (antes del mes actual + mes actual)
        Long eventosHastaAhora = publicacionRepository.countByTipoPublicacionAndVisibilidad(
                TipoPublicacion.EVENTO, true
        );

        // Calcular porcentaje de cambio
        TrendData trend = calcularTendencia(eventosHastaAhora, eventosAntesMesActual);

        return new DashboardStatResponse(
                "Total eventos",
                totalActual,
                trend,
                false
        );
    }

    private DashboardStatResponse obtenerEstadisticaNoticias() {
        // Total de noticias visibles
        Long totalActual = publicacionRepository.countByTipoPublicacionAndVisibilidad(
                TipoPublicacion.NOTICIA, true
        );

        // Noticias con fecha antes del mes actual
        LocalDateTime inicioMesActual = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        Long noticiasAntesMesActual = publicacionRepository.countByTipoPublicacionAndFechaLessThanAndVisibilidad(
                TipoPublicacion.NOTICIA, inicioMesActual, true
        );

        // Noticias hasta ahora (antes del mes actual + mes actual)
        Long noticiasHastaAhora = publicacionRepository.countByTipoPublicacionAndVisibilidad(
                TipoPublicacion.NOTICIA, true
        );

        // Calcular porcentaje de cambio
        TrendData trend = calcularTendencia(noticiasHastaAhora, noticiasAntesMesActual);

        return new DashboardStatResponse(
                "Total noticias",
                totalActual,
                trend,
                false
        );
    }

    private DashboardStatResponse obtenerEstadisticaIntegrantes() {
        // Total de integrantes visibles actuales
        Long totalActual = integranteRepository.countByVisibilidad(true);

        // No hay forma de comparar datos históricos de integrantes
        // Por lo tanto, retornamos null y false para el trend
        return new DashboardStatResponse(
                "Total integrantes",
                totalActual,
                new TrendData(null, false),
                false
        );
    }

    private TrendData calcularTendencia(Long actual, Long anterior) {
        if (anterior == 0) {
            // Si no hay datos anteriores, considerar como 100% de crecimiento si hay datos actuales
            if (actual > 0) {
                return new TrendData(100.0, true);
            } else {
                return new TrendData(0.0, true);
            }
        }

        // Calcular porcentaje de cambio: ((actual - anterior) / anterior) * 100
        double porcentaje = ((actual - anterior) * 100.0) / anterior;
        boolean esPositivo = porcentaje >= 0;

        // Redondear a 2 decimales
        porcentaje = Math.round(porcentaje * 100.0) / 100.0;

        return new TrendData(Math.abs(porcentaje), esPositivo);
    }
}
