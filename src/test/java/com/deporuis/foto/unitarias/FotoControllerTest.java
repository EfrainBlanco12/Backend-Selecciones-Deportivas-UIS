package com.deporuis.foto.unitarias;

import com.deporuis.Foto.aplicacion.FotoService;
import com.deporuis.Foto.infraestructura.FotoController;
import com.deporuis.Foto.infraestructura.dto.FotoRequest;
import com.deporuis.Foto.infraestructura.dto.FotoResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FotoControllerTest {

    @Mock
    private FotoService fotoService;

    @InjectMocks
    private FotoController controller;

    private FotoResponse response() {
        return new FotoResponse(1, "contenido123".getBytes(), 2024, 1, null, null);
    }

    private FotoRequest request() {
        FotoRequest req = new FotoRequest();
        req.setContenido("contenido123".getBytes());
        req.setTemporada(2024);
        req.setIdIntegrante(1);
        return req;
    }

    @Test
    void crearFoto_debeRetornar201Created() {
        FotoRequest req = request();
        FotoResponse resp = response();
        when(fotoService.crearFoto(req)).thenReturn(resp);

        ResponseEntity<FotoResponse> result = controller.crearFoto(req);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(1, result.getBody().getIdFoto());
        verify(fotoService).crearFoto(req);
    }

    @Test
    void obtenerFoto_debeRetornarFoto() {
        FotoResponse resp = response();
        when(fotoService.obtenerFoto(1)).thenReturn(resp);

        ResponseEntity<FotoResponse> result = controller.obtenerFoto(1);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().getIdFoto());
        verify(fotoService).obtenerFoto(1);
    }

    @Test
    void obtenerFoto_cuandoNoExiste_debeRetornar404() {
        when(fotoService.obtenerFoto(1)).thenReturn(null);

        ResponseEntity<FotoResponse> result = controller.obtenerFoto(1);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        verify(fotoService).obtenerFoto(1);
    }

    @Test
    void obtenerFotosPaginadas_debeRetornarPageDeFotos() {
        Page<FotoResponse> page = new PageImpl<>(List.of(response()));
        when(fotoService.obtenerFotosPaginadas(0, 5)).thenReturn(page);

        ResponseEntity<Page<FotoResponse>> result = controller.obtenerFotosPaginadas(0, 5);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().getContent().size());
        verify(fotoService).obtenerFotosPaginadas(0, 5);
    }

    @Test
    void obtenerFotosPorIntegrante_debeRetornarListaDeFotos() {
        List<FotoResponse> fotos = List.of(response());
        when(fotoService.obtenerFotosPorIntegrante(1)).thenReturn(fotos);

        ResponseEntity<List<FotoResponse>> result = controller.obtenerFotosPorIntegrante(1);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().size());
        verify(fotoService).obtenerFotosPorIntegrante(1);
    }

    @Test
    void obtenerFotosPorSeleccion_debeRetornarListaDeFotos() {
        List<FotoResponse> fotos = List.of(response());
        when(fotoService.obtenerFotosPorSeleccion(1)).thenReturn(fotos);

        ResponseEntity<List<FotoResponse>> result = controller.obtenerFotosPorSeleccion(1);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().size());
        verify(fotoService).obtenerFotosPorSeleccion(1);
    }

    @Test
    void actualizarFoto_debeRetornar200Ok() {
        FotoRequest req = request();
        FotoResponse resp = response();
        when(fotoService.actualizarFoto(1, req)).thenReturn(resp);

        ResponseEntity<FotoResponse> result = controller.actualizarFoto(1, req);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().getIdFoto());
        verify(fotoService).actualizarFoto(1, req);
    }

    @Test
    void eliminarFoto_debeRetornar204NoContent() {
        doNothing().when(fotoService).eliminarFoto(1);

        ResponseEntity<Void> result = controller.eliminarFoto(1);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        verify(fotoService).eliminarFoto(1);
    }
}
