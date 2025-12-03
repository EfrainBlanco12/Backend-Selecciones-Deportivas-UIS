package com.deporuis.seleccion.unitarias;

import com.deporuis.seleccion.aplicacion.SeleccionCommandService;
import com.deporuis.seleccion.aplicacion.SeleccionQueryService;
import com.deporuis.seleccion.aplicacion.SeleccionService;
import com.deporuis.seleccion.infraestructura.dto.SeleccionPatchRequest;
import com.deporuis.seleccion.infraestructura.dto.SeleccionRequest;
import com.deporuis.seleccion.infraestructura.dto.SeleccionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SeleccionServiceTest {

    @InjectMocks private SeleccionService service;
    @Mock private SeleccionCommandService commandService;
    @Mock private SeleccionQueryService queryService;

    private SeleccionRequest request;
    private SeleccionResponse response;

    @BeforeEach
    void setup() {
        request = new SeleccionRequest();
        response = new SeleccionResponse();
    }

    @Test
    void crearSeleccion_delegaEnCommand() {
        when(commandService.crearSeleccion(request, 100)).thenReturn(response);
        assertSame(response, service.crearSeleccion(request, 100));
        verify(commandService).crearSeleccion(request, 100);
        verifyNoMoreInteractions(commandService, queryService);
    }

    @Test
    void obtenerSeleccionesPaginadas_delegaEnQuery() {
        Page<SeleccionResponse> page = new PageImpl<>(List.of(new SeleccionResponse()));
        when(queryService.obtenerSeleccionesPaginadas(0, 5)).thenReturn(page);
        assertSame(page, service.obtenerSeleccionesPaginadas(0, 5));
        verify(queryService).obtenerSeleccionesPaginadas(0, 5);
        verifyNoMoreInteractions(commandService, queryService);
    }

    @Test
    void obtenerSeleccion_delegaEnQuery() {
        when(queryService.obtenerSeleccion(42)).thenReturn(response);
        assertSame(response, service.obtenerSeleccion(42));
        verify(queryService).obtenerSeleccion(42);
        verifyNoMoreInteractions(commandService, queryService);
    }

    @Test
    void eliminarSeleccion_delegaEnCommand() {
        doNothing().when(commandService).eliminarSeleccion(11);
        service.eliminarSeleccion(11);
        verify(commandService).eliminarSeleccion(11);
        verifyNoMoreInteractions(commandService, queryService);
    }

    @Test
    void softDeleteSeleccion_delegaEnCommand() {
        doNothing().when(commandService).softDeleteSeleccion(9);
        service.softDeleteSeleccion(9);
        verify(commandService).softDeleteSeleccion(9);
        verifyNoMoreInteractions(commandService, queryService);
    }

    @Test
    void actualizarSeleccion_delegaEnCommand() {
        when(commandService.actualizarSeleccion(7, request, 200)).thenReturn(response);
        assertSame(response, service.actualizarSeleccion(7, request, 200));
        verify(commandService).actualizarSeleccion(7, request, 200);
        verifyNoMoreInteractions(commandService, queryService);
    }

    @Test
    void actualizarSeleccionParcial_delegaEnCommand() {
        SeleccionPatchRequest patchRequest = new SeleccionPatchRequest();
        when(commandService.actualizarSeleccionParcial(8, patchRequest, 300)).thenReturn(response);
        assertSame(response, service.actualizarSeleccionParcial(8, patchRequest, 300));
        verify(commandService).actualizarSeleccionParcial(8, patchRequest, 300);
        verifyNoMoreInteractions(commandService, queryService);
    }
}
