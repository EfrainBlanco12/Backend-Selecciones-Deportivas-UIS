package com.deporuis.deporte.unitarias;

import com.deporuis.deporte.aplicacion.DeporteCommandService;
import com.deporuis.deporte.aplicacion.DeporteQueryService;
import com.deporuis.deporte.aplicacion.DeporteService;
import com.deporuis.deporte.infraestructura.dto.DeporteRequest;
import com.deporuis.deporte.infraestructura.dto.DeporteResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DeporteServiceTest {
    @Mock private DeporteCommandService commandService;
    @Mock private DeporteQueryService queryService;

    @InjectMocks
    private DeporteService service;

    @BeforeEach void init() { MockitoAnnotations.openMocks(this); }

    @Test
    void crearDeporte_delegaEnCommandService() {
        DeporteRequest req = new DeporteRequest();
        req.setNombreDeporte("Atletismo"); req.setDescripcionDeporte("Desc");
        DeporteResponse resp = new DeporteResponse(1, "Atletismo");
        when(commandService.crearDeporte(req)).thenReturn(resp);

        DeporteResponse out = service.crearDeporte(req);
        assertEquals(1, out.getIdDeporte());
        verify(commandService).crearDeporte(req);
    }

    @Test
    void obtenerTodosLosDeportesVisibles_delegaEnQueryService() {
        when(queryService.obtenerTodosLosDeportesVisibles()).thenReturn(List.of(new DeporteResponse(1, "A")));
        List<DeporteResponse> list = service.obtenerTodosLosDeportesVisibles();
        assertEquals(1, list.size());
        verify(queryService).obtenerTodosLosDeportesVisibles();
    }

    @Test
    void actualizarDeporte_delegaEnCommandService() {
        DeporteRequest req = new DeporteRequest();
        req.setNombreDeporte("Natación"); req.setDescripcionDeporte("Desc");
        DeporteResponse resp = new DeporteResponse(9, "Natación");
        when(commandService.actualizarDeporte(9, req)).thenReturn(resp);
        DeporteResponse out = service.actualizarDeporte(9, req);
        assertEquals(9, out.getIdDeporte());
        verify(commandService).actualizarDeporte(9, req);
    }

    @Test
    void softDeleteDeporte_delegaEnCommandService() {
        DeporteResponse resp = new DeporteResponse(2, "X");
        when(commandService.softDeleteDeporte(2)).thenReturn(resp);
        DeporteResponse out = service.softDeleteDeporte(2);
        assertEquals(2, out.getIdDeporte());
        verify(commandService).softDeleteDeporte(2);
    }
}
