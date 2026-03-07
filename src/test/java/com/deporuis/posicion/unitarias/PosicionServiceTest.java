package com.deporuis.posicion.unitarias;

import com.deporuis.deporte.dominio.Deporte;
import com.deporuis.posicion.aplicacion.PosicionCommandService;
import com.deporuis.posicion.aplicacion.PosicionQueryService;
import com.deporuis.posicion.aplicacion.PosicionService;
import com.deporuis.posicion.dominio.Posicion;
import com.deporuis.posicion.infraestructura.dto.PosicionActualizarRequest;
import com.deporuis.posicion.infraestructura.dto.PosicionRequest;
import com.deporuis.posicion.infraestructura.dto.PosicionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class PosicionServiceTest {

    @InjectMocks private PosicionService service;
    @Mock private PosicionCommandService command;
    @Mock private PosicionQueryService query;

    @BeforeEach void open() { MockitoAnnotations.openMocks(this); }

    private PosicionResponse resp() {
        // Construimos una Posicion con Deporte no nulo para evitar NPE en el constructor de PosicionResponse
        Deporte d = new Deporte();
        d.setIdDeporte(1);
        d.setNombreDeporte("Fútbol");

        Posicion p = new Posicion();
        p.setIdPosicion(123);
        p.setNombrePosicion("Central");
        p.setDeporte(d);
        p.setVisibilidad(true);

        return new PosicionResponse(p);
    }

    @Test
    void crear_delegaEnCommand() {
        PosicionRequest req = new PosicionRequest();
        PosicionResponse r = resp();

        when(command.crearPosicion(req)).thenReturn(r);

        assertEquals(r, service.crearPosicion(req));
        verify(command).crearPosicion(req);
    }

    @Test
    void obtenerPorDeporte_delegaEnQuery() {
        PosicionResponse r = resp();
        List<PosicionResponse> lista = List.of(r);

        when(query.obtenerPosicionPorDeporte(2)).thenReturn(lista);

        assertEquals(lista, service.obtenerPosicionPorDeporte(2));
        verify(query).obtenerPosicionPorDeporte(2);
    }

    @Test
    void actualizar_softDelete_deleganEnCommand() {
        PosicionActualizarRequest req = new PosicionActualizarRequest();
        PosicionResponse r = resp();

        when(command.actualizarPosicion(9, req)).thenReturn(r);
        assertEquals(r, service.actualizarPosicion(9, req));
        verify(command).actualizarPosicion(9, req);

        when(command.softDelete(7)).thenReturn(r);
        assertEquals(r, service.softDelete(7));
        verify(command).softDelete(7);
    }
}
