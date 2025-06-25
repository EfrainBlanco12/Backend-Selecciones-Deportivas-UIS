package com.deporuis.horario.infraestructura.dto;

import com.deporuis.horario.dominio.DiaHorario;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalTime;
import java.util.List;

@Data
@AllArgsConstructor
public class HorarioResponse {
    private Integer idHorario;

    private DiaHorario dia;

    private LocalTime horaInicio;

    private LocalTime horaFin;

    private List<Integer> selecciones;
}
