package com.deporuis.horario.unitarias;

import com.deporuis.horario.aplicacion.mapper.HorarioMapper;
import com.deporuis.horario.dominio.DiaHorario;
import com.deporuis.horario.dominio.Horario;
import com.deporuis.horario.infraestructura.dto.HorarioRequest;
import com.deporuis.horario.infraestructura.dto.HorarioResponse;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HorarioMapperTest {

    @Test
    void requestToHorario_debeConvertirCorrectamente() {
        HorarioRequest request = new HorarioRequest();
        request.setDia(DiaHorario.LUNES);
        request.setHoraInicio(LocalTime.of(8, 0));
        request.setHoraFin(LocalTime.of(10, 0));

        Horario horario = HorarioMapper.requestToHorario(request);

        assertNotNull(horario);
        assertEquals(DiaHorario.LUNES, horario.getDia());
        assertEquals(LocalTime.of(8, 0), horario.getHoraInicio());
        assertEquals(LocalTime.of(10, 0), horario.getHoraFin());
    }

    @Test
    void requestToHorariosSeleccion_debeConvertirListaCorrectamente() {
        List<HorarioRequest> requests = List.of(
                createRequest(DiaHorario.LUNES, 8, 0, 10, 0),
                createRequest(DiaHorario.MIERCOLES, 14, 0, 16, 0)
        );

        List<Horario> horarios = HorarioMapper.requestToHorariosSeleccion(requests);

        assertNotNull(horarios);
        assertEquals(2, horarios.size());
        assertEquals(DiaHorario.LUNES, horarios.get(0).getDia());
        assertEquals(DiaHorario.MIERCOLES, horarios.get(1).getDia());
    }

    @Test
    void toResponse_debeConvertirCorrectamente() {
        Horario horario = new Horario();
        horario.setIdHorario(1);
        horario.setDia(DiaHorario.MARTES);
        horario.setHoraInicio(LocalTime.of(9, 0));
        horario.setHoraFin(LocalTime.of(11, 0));
        horario.setSelecciones(new ArrayList<>());

        HorarioResponse response = HorarioMapper.toResponse(horario);

        assertNotNull(response);
        assertEquals(1, response.getIdHorario());
        assertEquals(DiaHorario.MARTES, response.getDia());
        assertEquals(LocalTime.of(9, 0), response.getHoraInicio());
        assertEquals(LocalTime.of(11, 0), response.getHoraFin());
        assertNotNull(response.getSelecciones());
        assertTrue(response.getSelecciones().isEmpty());
    }

    private HorarioRequest createRequest(DiaHorario dia, int horaInicioH, int horaInicioM, int horaFinH, int horaFinM) {
        HorarioRequest req = new HorarioRequest();
        req.setDia(dia);
        req.setHoraInicio(LocalTime.of(horaInicioH, horaInicioM));
        req.setHoraFin(LocalTime.of(horaFinH, horaFinM));
        return req;
    }
}
