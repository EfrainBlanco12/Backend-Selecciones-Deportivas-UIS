package com.deporuis.publicacion.unitarias;

import com.deporuis.publicacion.aplicacion.PublicacionService;
import com.deporuis.publicacion.dominio.TipoPublicacion;
import com.deporuis.publicacion.infraestructura.PublicacionController;
import com.deporuis.publicacion.infraestructura.dto.PublicacionRequest;
import com.deporuis.publicacion.infraestructura.dto.PublicacionResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PublicacionControllerTest {

    @Mock
    private PublicacionService publicacionService;

    @InjectMocks
    private PublicacionController controller;

    private PublicacionResponse response() {
        PublicacionResponse resp = new PublicacionResponse();
        resp.setIdPublicacion(1);
        resp.setTitulo("Título");
        resp.setDescripcion("Descripción");
        resp.setLugar("Campus UIS");
        resp.setFecha(LocalDateTime.of(2024, 5, 20, 10, 0));
        resp.setDuracion("2 horas");
        resp.setTipoPublicacion(TipoPublicacion.NOTICIA.name());
        resp.setVisibilidad(true);
        resp.setUsuarioModifico(100);
        resp.setFotos(List.of());
        resp.setIdSelecciones(List.of());
        return resp;
    }

    private PublicacionRequest request() {
        PublicacionRequest req = new PublicacionRequest();
        req.setTitulo("Título");
        req.setDescripcion("Descripción");
        req.setLugar("Campus UIS");
        req.setFecha(LocalDateTime.of(2024, 5, 20, 10, 0));
        req.setDuracion("2 horas");
        req.setTipoPublicacion(TipoPublicacion.NOTICIA);
        req.setSelecciones(List.of(1));
        return req;
    }

    @Test
    void crearPublicacion_debeRetornar201Created() {
        PublicacionRequest req = request();
        PublicacionResponse resp = response();
        when(publicacionService.crearPublicacion(req, 100)).thenReturn(resp);

        ResponseEntity<PublicacionResponse> result = controller.crearPublicacion(req, 100);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(1, result.getBody().getIdPublicacion());
        verify(publicacionService).crearPublicacion(req, 100);
    }

    @Test
    void obtenerEventoPorId_debeRetornarPublicacion() {
        PublicacionResponse resp = response();
        when(publicacionService.obtenerPublicacion(1)).thenReturn(resp);

        ResponseEntity<PublicacionResponse> result = controller.obtenerEventoPorId(1);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().getIdPublicacion());
        verify(publicacionService).obtenerPublicacion(1);
    }

    @Test
    void obtenerPublicacionesPaginadas_debeRetornarPageDePublicaciones() {
        Page<PublicacionResponse> page = new PageImpl<>(List.of(response()));
        when(publicacionService.obtenerPublicacionesPaginadas(0, 5)).thenReturn(page);

        ResponseEntity<Page<PublicacionResponse>> result = controller.obtenerPublicacionesPaginadas(0, 5);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().getContent().size());
        verify(publicacionService).obtenerPublicacionesPaginadas(0, 5);
    }

    @Test
    void obtenerNoticiasPaginadas_debeRetornarPageDeNoticias() {
        Page<PublicacionResponse> page = new PageImpl<>(List.of(response()));
        when(publicacionService.obtenerNoticiasPaginadas(0, 5)).thenReturn(page);

        ResponseEntity<Page<PublicacionResponse>> result = controller.obtenerNoticiasPaginadas(0, 5);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().getContent().size());
        verify(publicacionService).obtenerNoticiasPaginadas(0, 5);
    }

    @Test
    void obtenerEventosPaginados_debeRetornarPageDeEventos() {
        Page<PublicacionResponse> page = new PageImpl<>(List.of(response()));
        when(publicacionService.obtenerEventosPaginados(0, 5)).thenReturn(page);

        ResponseEntity<Page<PublicacionResponse>> result = controller.obtenerEventosPaginados(0, 5);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().getContent().size());
        verify(publicacionService).obtenerEventosPaginados(0, 5);
    }

    @Test
    void actualizarPublicacion_debeRetornar200Ok() {
        PublicacionRequest req = request();
        PublicacionResponse resp = response();
        when(publicacionService.actualizarPublicacion(1, req, 100)).thenReturn(resp);

        ResponseEntity<PublicacionResponse> result = controller.actualizarPublicacion(1, req, 100);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().getIdPublicacion());
        verify(publicacionService).actualizarPublicacion(1, req, 100);
    }

    @Test
    void softDeletePublicacion_debeRetornar204NoContent() {
        doNothing().when(publicacionService).softDeletePublicacion(1);

        ResponseEntity<Void> result = controller.softDeletePublicacion(1);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        verify(publicacionService).softDeletePublicacion(1);
    }
}
