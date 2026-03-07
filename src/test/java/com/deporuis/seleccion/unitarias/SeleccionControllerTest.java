package com.deporuis.seleccion.unitarias;

import com.deporuis.seleccion.infraestructura.SeleccionController;
import com.deporuis.seleccion.aplicacion.SeleccionService;
import com.deporuis.seleccion.infraestructura.dto.SeleccionPatchRequest;
import com.deporuis.seleccion.infraestructura.dto.SeleccionRequest;
import com.deporuis.seleccion.infraestructura.dto.SeleccionResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SeleccionControllerTest {

    @InjectMocks private SeleccionController controller;
    @Mock private SeleccionService service;

    @Test
    void crearSeleccion_retornaBody() {
        SeleccionRequest request = new SeleccionRequest();
        SeleccionResponse esperado = new SeleccionResponse();
        when(service.crearSeleccion(request, 123)).thenReturn(esperado);
        ResponseEntity<SeleccionResponse> resp = controller.crearSeleccion(request, 123);
        assertNotNull(resp);
        assertSame(esperado, resp.getBody());
        verify(service).crearSeleccion(request, 123);
        verifyNoMoreInteractions(service);
    }

    @Test
    void obtenerSeleccionesPaginadas_retornaPagina() {
        Page<SeleccionResponse> page = new PageImpl<>(List.of(new SeleccionResponse()));
        when(service.obtenerSeleccionesPaginadas(0, 10)).thenReturn(page);
        ResponseEntity<Page<SeleccionResponse>> resp = controller.obtenerSeleccionesPaginadas(0, 10);
        assertNotNull(resp);
        assertSame(page, resp.getBody());
        verify(service).obtenerSeleccionesPaginadas(0, 10);
        verifyNoMoreInteractions(service);
    }

    @Test
    void obtenerSeleccion_retornaOk() {
        SeleccionResponse esperado = new SeleccionResponse();
        when(service.obtenerSeleccion(100)).thenReturn(esperado);
        ResponseEntity<SeleccionResponse> resp = controller.obtenerSeleccion(100);
        assertNotNull(resp);
        assertSame(esperado, resp.getBody());
        verify(service).obtenerSeleccion(100);
        verifyNoMoreInteractions(service);
    }

    @Test
    void eliminarSeleccion_ok() {
        doNothing().when(service).eliminarSeleccion(5);
        ResponseEntity<Void> resp = controller.eliminarSeleccion(5);
        assertNotNull(resp);
        verify(service).eliminarSeleccion(5);
        verifyNoMoreInteractions(service);
    }

    @Test
    void softDeleteSeleccion_ok() {
        doNothing().when(service).softDeleteSeleccion(6);
        ResponseEntity<Void> resp = controller.softDeleteSeleccion(6);
        assertNotNull(resp);
        verify(service).softDeleteSeleccion(6);
        verifyNoMoreInteractions(service);
    }

    @Test
    void actualizarSeleccion_ok() {
        SeleccionRequest request = new SeleccionRequest();
        SeleccionResponse actualizado = new SeleccionResponse();
        when(service.actualizarSeleccion(9, request, 456)).thenReturn(actualizado);
        ResponseEntity<SeleccionResponse> resp = controller.actualizarSeleccion(9, request, 456);
        assertNotNull(resp);
        assertSame(actualizado, resp.getBody());
        verify(service).actualizarSeleccion(9, request, 456);
        verifyNoMoreInteractions(service);
    }

    @Test
    void actualizarSeleccionParcial_ok() {
        SeleccionPatchRequest patchRequest = new SeleccionPatchRequest();
        SeleccionResponse actualizado = new SeleccionResponse();
        when(service.actualizarSeleccionParcial(10, patchRequest, 789)).thenReturn(actualizado);
        ResponseEntity<SeleccionResponse> resp = controller.actualizarSeleccionParcial(10, patchRequest, 789);
        assertNotNull(resp);
        assertSame(actualizado, resp.getBody());
        verify(service).actualizarSeleccionParcial(10, patchRequest, 789);
        verifyNoMoreInteractions(service);
    }
}
