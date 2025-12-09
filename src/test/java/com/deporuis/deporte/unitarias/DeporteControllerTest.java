package com.deporuis.deporte.unitarias;

import com.deporuis.deporte.aplicacion.DeporteService;
import com.deporuis.deporte.infraestructura.DeporteController;
import com.deporuis.deporte.infraestructura.dto.DeporteRequest;
import com.deporuis.deporte.infraestructura.dto.DeporteResponse;
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
class DeporteControllerTest {

    @Mock
    private DeporteService deporteService;

    @InjectMocks
    private DeporteController controller;

    private DeporteRequest request() {
        DeporteRequest req = new DeporteRequest();
        req.setNombreDeporte("Fútbol");
        req.setDescripcionDeporte("Descripción del fútbol");
        return req;
    }

    private DeporteResponse response() {
        return new DeporteResponse(1, "Fútbol");
    }

    @Test
    void crearDeporte_debeRetornar201Created() {
        DeporteRequest req = request();
        DeporteResponse resp = response();
        when(deporteService.crearDeporte(req)).thenReturn(resp);

        ResponseEntity<DeporteResponse> result = controller.crearDeporte(req);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(1, result.getBody().getIdDeporte());
        assertEquals("Fútbol", result.getBody().getNombreDeporte());
        verify(deporteService).crearDeporte(req);
    }

    @Test
    void obtenerTodosLosDeportesVisibles_debeRetornarListaDeDeportes() {
        List<DeporteResponse> deportes = List.of(
                new DeporteResponse(1, "Fútbol"),
                new DeporteResponse(2, "Baloncesto")
        );
        when(deporteService.obtenerTodosLosDeportesVisibles()).thenReturn(deportes);

        ResponseEntity<List<DeporteResponse>> result = controller.obtenerTodosLosDeportesVisibles();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(2, result.getBody().size());
        verify(deporteService).obtenerTodosLosDeportesVisibles();
    }

    @Test
    void actualizarDeporte_debeRetornar200Ok() {
        DeporteRequest req = request();
        DeporteResponse resp = response();
        when(deporteService.actualizarDeporte(1, req)).thenReturn(resp);

        ResponseEntity<DeporteResponse> result = controller.actualizarDeporte(1, req);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().getIdDeporte());
        verify(deporteService).actualizarDeporte(1, req);
    }

    @Test
    void softDeleteDeporte_debeRetornar200Ok() {
        DeporteResponse resp = response();
        when(deporteService.softDeleteDeporte(1)).thenReturn(resp);

        ResponseEntity<DeporteResponse> result = controller.softDeleteDeporte(1);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().getIdDeporte());
        verify(deporteService).softDeleteDeporte(1);
    }
}
