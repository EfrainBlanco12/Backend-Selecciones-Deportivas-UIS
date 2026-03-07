package com.deporuis.logro.unitarias;

import com.deporuis.logro.aplicacion.LogroService;
import com.deporuis.logro.infraestructura.LogroController;
import com.deporuis.logro.infraestructura.dto.LogroRequest;
import com.deporuis.logro.infraestructura.dto.LogroResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LogroControllerTest {

    @Mock
    private LogroService logroService;

    @InjectMocks
    private LogroController controller;

    private LogroResponse response() {
        return new LogroResponse(1, LocalDate.of(2024, 5, 20), "Título Regional", "Descripción", List.of(1, 2));
    }

    private LogroRequest request() {
        LogroRequest req = new LogroRequest();
        req.setFechaLogro(LocalDate.of(2024, 5, 20));
        req.setNombreLogro("Título Regional");
        req.setDescripcionLogro("Descripción");
        req.setSelecciones(List.of(1, 2));
        return req;
    }

    @Test
    void crearLogro_debeRetornar201Created() {
        LogroRequest req = request();
        LogroResponse resp = response();
        when(logroService.crearLogro(req)).thenReturn(resp);

        ResponseEntity<LogroResponse> result = controller.crearLogro(req);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(1, result.getBody().getIdLogro());
        assertEquals("Título Regional", result.getBody().getNombreLogro());
        verify(logroService).crearLogro(req);
    }

    @Test
    void obtenerLogrosPaginados_debeRetornarPageDeLogros() {
        Page<LogroResponse> page = new PageImpl<>(List.of(response()));
        when(logroService.obtenerLogrosPaginados(0, 4)).thenReturn(page);

        ResponseEntity<Page<LogroResponse>> result = controller.obtenerLogrosPaginados(0, 4);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().getContent().size());
        verify(logroService).obtenerLogrosPaginados(0, 4);
    }

    @Test
    void obtenerLogro_debeRetornarLogro() {
        LogroResponse resp = response();
        when(logroService.obtenerLogro(1)).thenReturn(resp);

        ResponseEntity<LogroResponse> result = controller.obtenerLogro(1);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().getIdLogro());
        verify(logroService).obtenerLogro(1);
    }

    @Test
    void obtenerLogro_cuandoNoExiste_debeRetornar404() {
        when(logroService.obtenerLogro(1)).thenReturn(null);

        ResponseEntity<LogroResponse> result = controller.obtenerLogro(1);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        verify(logroService).obtenerLogro(1);
    }

    @Test
    void actualizarLogro_debeRetornar200Ok() {
        LogroRequest req = request();
        LogroResponse resp = response();
        when(logroService.actualizarLogro(1, req)).thenReturn(resp);

        ResponseEntity<LogroResponse> result = controller.actualizarLogro(1, req);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().getIdLogro());
        verify(logroService).actualizarLogro(1, req);
    }

    @Test
    void eliminarLogro_debeRetornar204NoContent() {
        doNothing().when(logroService).eliminarLogro(1);

        ResponseEntity<Void> result = controller.eliminarLogro(1);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        verify(logroService).eliminarLogro(1);
    }
}
