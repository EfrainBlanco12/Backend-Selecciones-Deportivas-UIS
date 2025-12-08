package com.deporuis.shared.dashboard.infraestructura;

import com.deporuis.shared.dashboard.aplicacion.DashboardService;
import com.deporuis.shared.dashboard.dominio.StatType;
import com.deporuis.shared.dashboard.infraestructura.dto.DashboardStatResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/public/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/stats/{tipo}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<DashboardStatResponse> obtenerEstadistica(
            @PathVariable StatType tipo
    ) {
        DashboardStatResponse estadistica = dashboardService.obtenerEstadistica(tipo);
        return ResponseEntity.ok(estadistica);
    }
}
