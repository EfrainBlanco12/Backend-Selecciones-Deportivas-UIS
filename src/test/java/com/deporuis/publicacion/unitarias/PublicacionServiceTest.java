package com.deporuis.publicacion.unitarias;

import com.deporuis.publicacion.aplicacion.PublicacionCommandService;
import com.deporuis.publicacion.aplicacion.PublicacionQueryService;
import com.deporuis.publicacion.aplicacion.PublicacionService;
import com.deporuis.publicacion.infraestructura.dto.PublicacionRequest;
import com.deporuis.publicacion.infraestructura.dto.PublicacionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class PublicacionServiceTest {

    @InjectMocks private PublicacionService service;
    @Mock private PublicacionCommandService command;
    @Mock private PublicacionQueryService query;

    @BeforeEach void open() { MockitoAnnotations.openMocks(this); }

    @Test
    void crear_delegaEnCommand() {
        PublicacionRequest req = new PublicacionRequest();
        PublicacionResponse resp = new PublicacionResponse(1, "t", "d", "l", null, "x", null, List.of(), List.of());
        when(command.crearPublicacion(req)).thenReturn(resp);

        assertEquals(resp, service.crearPublicacion(req));
        verify(command).crearPublicacion(req);
    }

    @Test
    void obtenerPorId_delegaEnQuery() {
        PublicacionResponse r = new PublicacionResponse(1, "t", "d", "l", null, "x", null, List.of(), List.of());
        when(query.obtenerPublicacion(1)).thenReturn(r);

        assertEquals(r, service.obtenerPublicacion(1));
        verify(query).obtenerPublicacion(1);
    }

    @Test
    void obtenerPaginadas_delegaEnQuery() {
        Page<PublicacionResponse> page = new PageImpl<>(List.of(
                new PublicacionResponse(1, "t", "d", "l", null, "x", null, List.of(), List.of())
        ));
        when(query.obtenerPublicacionesPaginadas(0, 5)).thenReturn(page);

        assertEquals(page, service.obtenerPublicacionesPaginadas(0, 5));
        verify(query).obtenerPublicacionesPaginadas(0, 5);
    }

    @Test
    void actualizar_eliminar_softDelete_deleganEnCommand() {
        PublicacionRequest req = new PublicacionRequest();
        PublicacionResponse resp = new PublicacionResponse(1, "t", "d", "l", null, "x", null, List.of(), List.of());

        when(command.actualizarPublicacion(2, req)).thenReturn(resp);
        assertEquals(resp, service.actualizarPublicacion(2, req));
        verify(command).actualizarPublicacion(2, req);

        service.eliminarPublicacion(3);
        verify(command).eliminarPublicacion(3);

        service.softDeletePublicacion(4);
        verify(command).softDeletePublicacion(4);
    }
}
