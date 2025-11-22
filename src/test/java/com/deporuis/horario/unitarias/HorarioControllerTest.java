package com.deporuis.horario.unitarias;

import com.deporuis.horario.aplicacion.HorarioService;
import com.deporuis.horario.dominio.DiaHorario;
import com.deporuis.horario.infraestructura.HorarioController;
import com.deporuis.horario.infraestructura.dto.HorarioRequest;
import com.deporuis.horario.infraestructura.dto.HorarioResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;

import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HorarioControllerTest {

    @InjectMocks private HorarioController controller;
    @Mock private HorarioService service;

    private HorarioRequest req() {
        HorarioRequest r = new HorarioRequest();
        r.setDia(DiaHorario.LUNES);
        r.setHoraInicio(LocalTime.of(8,0));
        r.setHoraFin(LocalTime.of(10,0));
        return r;
    }

    private HorarioResponse resp() {
        return new HorarioResponse(5, DiaHorario.LUNES, LocalTime.of(8,0), LocalTime.of(10,0), List.of());
    }

    @Test
    void crearHorario_retornaBody() {
        HorarioRequest request = req();
        HorarioResponse esperado = resp();
        when(service.crearHorario(request)).thenReturn(esperado);

        ResponseEntity<HorarioResponse> result = controller.crearHorario(request);

        assertNotNull(result);
        assertSame(esperado, result.getBody());
        verify(service).crearHorario(request);
        verifyNoMoreInteractions(service);
    }

    @Test
    void obtenerHorario_retornaOk() {
        HorarioResponse esperado = resp();
        when(service.obtenerHorario(10)).thenReturn(esperado);

        ResponseEntity<HorarioResponse> result = controller.obtenerHorario(10);

        assertNotNull(result);
        assertSame(esperado, result.getBody());
        verify(service).obtenerHorario(10);
        verifyNoMoreInteractions(service);
    }

    @Test
    void obtenerHorariosPaginados_retornaPagina() {
        Page<HorarioResponse> page = new PageImpl<>(List.of(resp()));
        when(service.obtenerHorariosPaginados(0, 5)).thenReturn(page);

        ResponseEntity<Page<HorarioResponse>> result = controller.obtenerHorariosPaginados(0, 5);

        assertNotNull(result);
        assertSame(page, result.getBody());
        verify(service).obtenerHorariosPaginados(0, 5);
        verifyNoMoreInteractions(service);
    }

    @Test
    void actualizarHorario_retornaOk() {
        HorarioRequest request = req();
        HorarioResponse actualizado = resp();
        when(service.actualizarHorario(15, request)).thenReturn(actualizado);

        ResponseEntity<HorarioResponse> result = controller.actualizarHorario(15, request);

        assertNotNull(result);
        assertSame(actualizado, result.getBody());
        verify(service).actualizarHorario(15, request);
        verifyNoMoreInteractions(service);
    }

    @Test
    void eliminarHorario_retornaNoContent() {
        doNothing().when(service).eliminarHorario(10);

        ResponseEntity<Void> result = controller.eliminarHorario(10);

        assertNotNull(result);
        assertEquals(204, result.getStatusCode().value());
        verify(service).eliminarHorario(10);
        verifyNoMoreInteractions(service);
    }
}
