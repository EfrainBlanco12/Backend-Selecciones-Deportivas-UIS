package com.deporuis.integrante.unitarias;

import com.deporuis.Foto.infraestructura.dto.FotoResponse;
import com.deporuis.auth.infraestructura.dto.RolResponse;
import com.deporuis.integrante.aplicacion.IntegranteService;
import com.deporuis.integrante.infraestructura.IntegranteController;
import com.deporuis.integrante.infraestructura.dto.IntegranteRequest;
import com.deporuis.integrante.infraestructura.dto.IntegranteResponse;
import com.deporuis.posicion.infraestructura.dto.PosicionResponse;
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
class IntegranteControllerTest {

    @Mock
    private IntegranteService integranteService;

    @InjectMocks
    private IntegranteController controller;

    private IntegranteResponse response() {
        RolResponse rol = new RolResponse(3, "DEPORTISTA");
        FotoResponse foto = new FotoResponse(1, "contenido123".getBytes(), 2024, 1, null, null);
        PosicionResponse posicion = mock(PosicionResponse.class);
        lenient().when(posicion.getIdPosicion()).thenReturn(1);
        lenient().when(posicion.getNombrePosicion()).thenReturn("Delantero");
        
        IntegranteResponse resp = new IntegranteResponse();
        resp.setIdIntegrante(1);
        resp.setCodigoUniversitario("U123");
        resp.setNombres("Juan");
        resp.setApellidos("Pérez");
        resp.setFechaNacimiento(LocalDate.of(2000, 1, 1));
        resp.setAltura(1.75f);
        resp.setPeso(70.0f);
        resp.setDorsal(10);
        resp.setCorreoUniversitario("juan@correo.uis.edu.co");
        resp.setIdSeleccion(1);
        resp.setRol(rol);
        resp.setFotos(List.of(foto));
        resp.setPosiciones(List.of(posicion));
        return resp;
    }

    private IntegranteRequest request() {
        IntegranteRequest req = new IntegranteRequest();
        req.setCodigoUniversitario("U123");
        req.setNombres("Juan");
        req.setApellidos("Pérez");
        req.setFechaNacimiento(LocalDate.of(2000, 1, 1));
        req.setAltura(1.75f);
        req.setPeso(70.0f);
        req.setDorsal(10);
        req.setCorreoInstitucional("juan@correo.uis.edu.co");
        req.setIdRol(3);
        req.setIdSeleccion(1);
        req.setIdPosiciones(List.of(1));
        return req;
    }

    @Test
    void crearIntegrante_debeRetornar201Created() {
        IntegranteRequest req = request();
        IntegranteResponse resp = response();
        when(integranteService.crearIntegrante(req)).thenReturn(resp);

        ResponseEntity<IntegranteResponse> result = controller.crearIntegrante(req);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(1, result.getBody().getIdIntegrante());
        assertEquals("Juan", result.getBody().getNombres());
        verify(integranteService).crearIntegrante(req);
    }

    @Test
    void obtenerIntegrantesPaginados_debeRetornarPageDeIntegrantes() {
        Page<IntegranteResponse> page = new PageImpl<>(List.of(response()));
        when(integranteService.obtenerIntegrantesPaginados(0, 6)).thenReturn(page);

        ResponseEntity<Page<IntegranteResponse>> result = controller.obtenerIntegrantesPaginados(0, 6);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().getContent().size());
        verify(integranteService).obtenerIntegrantesPaginados(0, 6);
    }

    @Test
    void obtenerIntegrante_debeRetornarIntegrante() {
        IntegranteResponse resp = response();
        when(integranteService.obtenerIntegrante(1)).thenReturn(resp);

        ResponseEntity<IntegranteResponse> result = controller.obtenerIntegrante(1);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().getIdIntegrante());
        verify(integranteService).obtenerIntegrante(1);
    }

    @Test
    void obtenerIntegrante_cuandoNoExiste_debeRetornar404() {
        when(integranteService.obtenerIntegrante(1)).thenReturn(null);

        ResponseEntity<IntegranteResponse> result = controller.obtenerIntegrante(1);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        verify(integranteService).obtenerIntegrante(1);
    }

    @Test
    void obtenerIntegrantePorCodigoUniversitario_debeRetornarIntegrante() {
        IntegranteResponse resp = response();
        when(integranteService.obtenerIntegrantePorCodigoUniversitario("U123")).thenReturn(resp);

        ResponseEntity<IntegranteResponse> result = controller.obtenerIntegrantePorCodigoUniversitario("U123");

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("U123", result.getBody().getCodigoUniversitario());
        verify(integranteService).obtenerIntegrantePorCodigoUniversitario("U123");
    }

    @Test
    void softDeleteIntegrante_debeRetornar204NoContent() {
        doNothing().when(integranteService).softDeleteIntegrante(1);

        ResponseEntity<Void> result = controller.softDeleteIntegrante(1);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        verify(integranteService).softDeleteIntegrante(1);
    }

    @Test
    void actualizarIntegrante_debeRetornar200Ok() {
        IntegranteRequest req = request();
        IntegranteResponse resp = response();
        when(integranteService.actualizarIntegrante(1, req)).thenReturn(resp);

        ResponseEntity<IntegranteResponse> result = controller.actualizarIntegrante(1, req);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().getIdIntegrante());
        verify(integranteService).actualizarIntegrante(1, req);
    }

    @Test
    void contarIntegrantes_debeRetornarConteo() {
        when(integranteService.contarIntegrantesPorSeleccion(1)).thenReturn(15L);

        ResponseEntity<Long> result = controller.contarIntegrantes(1);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(15L, result.getBody());
        verify(integranteService).contarIntegrantesPorSeleccion(1);
    }
}
