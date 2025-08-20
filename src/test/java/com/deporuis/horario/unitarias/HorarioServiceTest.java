package com.deporuis.horario.unitarias;

import com.deporuis.horario.aplicacion.HorarioCommandService;
import com.deporuis.horario.aplicacion.HorarioQueryService;
import com.deporuis.horario.aplicacion.HorarioService;
import com.deporuis.horario.infraestructura.dto.HorarioRequest;
import com.deporuis.horario.infraestructura.dto.HorarioResponse;
import com.deporuis.horario.dominio.DiaHorario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HorarioServiceTest {

    @Mock private HorarioCommandService command;
    @Mock private HorarioQueryService query;
    @InjectMocks private HorarioService service;

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
    void crear_delegaEnCommand() {
        when(command.crearHorario(any())).thenReturn(resp());
        HorarioResponse out = service.crearHorario(req());
        assertEquals(5, out.getIdHorario());
        verify(command).crearHorario(any());
    }

    @Test
    void obtenerPorId_delegaEnQuery() {
        when(query.obtenerHorario(9)).thenReturn(resp());
        HorarioResponse out = service.obtenerHorario(9);
        assertNotNull(out);
        verify(query).obtenerHorario(9);
    }

    @Test
    void lista_delegaEnQuery() {
        Page<HorarioResponse> page = new PageImpl<>(List.of(resp()));
        when(query.obtenerHorariosPaginados(0,5)).thenReturn(page);
        Page<HorarioResponse> out = service.obtenerHorariosPaginados(0,5);
        assertEquals(1, out.getTotalElements());
        verify(query).obtenerHorariosPaginados(0,5);
    }
}