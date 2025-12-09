package com.deporuis.posicion.unitarias;

import com.deporuis.posicion.aplicacion.PosicionService;
import com.deporuis.posicion.infraestructura.PosicionController;
import com.deporuis.posicion.infraestructura.dto.PosicionActualizarRequest;
import com.deporuis.posicion.infraestructura.dto.PosicionRequest;
import com.deporuis.posicion.infraestructura.dto.PosicionResponse;
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
class PosicionControllerTest {

    @Mock
    private PosicionService service;

    @InjectMocks
    private PosicionController controller;

    private PosicionResponse response() {
        PosicionResponse resp = mock(PosicionResponse.class);
        lenient().when(resp.getIdPosicion()).thenReturn(1);
        lenient().when(resp.getNombrePosicion()).thenReturn("Delantero");
        lenient().when(resp.getNombreDeporte()).thenReturn("Fútbol");
        return resp;
    }

    @Test
    void crearPosicion_debeRetornar201Created() {
        PosicionRequest req = new PosicionRequest();
        req.setNombrePosicion("Delantero");
        req.setIdDeporte(1);
        
        PosicionResponse resp = response();
        when(service.crearPosicion(req)).thenReturn(resp);

        ResponseEntity<PosicionResponse> result = controller.crearPosicion(req);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(1, result.getBody().getIdPosicion());
        assertEquals("Delantero", result.getBody().getNombrePosicion());
        verify(service).crearPosicion(req);
    }

    @Test
    void listarPosicionPorDeporte_debeRetornarListaDePosiciones() {
        PosicionResponse pos1 = mock(PosicionResponse.class);
        lenient().when(pos1.getIdPosicion()).thenReturn(1);
        lenient().when(pos1.getNombrePosicion()).thenReturn("Delantero");
        PosicionResponse pos2 = mock(PosicionResponse.class);
        lenient().when(pos2.getIdPosicion()).thenReturn(2);
        lenient().when(pos2.getNombrePosicion()).thenReturn("Defensa");
        List<PosicionResponse> posiciones = List.of(pos1, pos2);
        when(service.obtenerPosicionPorDeporte(1)).thenReturn(posiciones);

        ResponseEntity<List<PosicionResponse>> result = controller.listarPosicionPorDeporte(1);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(2, result.getBody().size());
        verify(service).obtenerPosicionPorDeporte(1);
    }

    @Test
    void actualizarPosicion_debeRetornar200Ok() {
        PosicionActualizarRequest req = new PosicionActualizarRequest();
        req.setNombrePosicion("Portero");
        
        PosicionResponse resp = response();
        when(service.actualizarPosicion(1, req)).thenReturn(resp);

        ResponseEntity<PosicionResponse> result = controller.actualizarPosicion(1, req);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().getIdPosicion());
        verify(service).actualizarPosicion(1, req);
    }

    @Test
    void eliminar_debeRetornar200Ok() {
        PosicionResponse resp = response();
        when(service.softDelete(1)).thenReturn(resp);

        ResponseEntity<PosicionResponse> result = controller.eliminar(1);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().getIdPosicion());
        verify(service).softDelete(1);
    }
}
