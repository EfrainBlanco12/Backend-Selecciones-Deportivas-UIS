package com.deporuis.horario.aplicacion.mapper;

import com.deporuis.horario.dominio.Horario;
import com.deporuis.horario.infraestructura.dto.HorarioRequest;
import com.deporuis.horario.infraestructura.dto.HorarioResponse;

public class HorarioMapper {

    public static Horario requestToHorario(HorarioRequest request) {
        return new Horario(
                request.getDia(),
                request.getHoraInicio(),
                request.getHoraFin()
        );
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
