package com.deporuis.horario.aplicacion.mapper;

import com.deporuis.horario.dominio.Horario;
import com.deporuis.horario.infraestructura.dto.HorarioRequest;
import com.deporuis.horario.infraestructura.dto.HorarioResponse;

import java.util.List;

public class HorarioMapper {

    public static Horario requestToHorario(HorarioRequest request) {
        return new Horario(
                request.getDia(),
                request.getHoraInicio(),
                request.getHoraFin()
        );
    }

    public static List<Horario> requestToHorariosSeleccion(List<HorarioRequest> horarioRequest) {
        return horarioRequest.stream()
                .map(HorarioMapper::requestToHorario)
                .toList();
    }

    public static HorarioResponse toResponse(Horario horario) {
        return new HorarioResponse(
                horario.getIdHorario(),
                horario.getDia(),
                horario.getHoraInicio(),
                horario.getHoraFin(),
                horario.getSelecciones().stream().map(seleccionHorario -> seleccionHorario.getSeleccion().getIdSeleccion()).toList()
        );
    }
}
