package com.deporuis.horario.infraestructura.dto;

import com.deporuis.horario.dominio.DiaHorario;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalTime;

@Data
public class HorarioRequest {

    @NotNull(message = "El dia del horario es obligatorio")
    private DiaHorario dia = DiaHorario.LUNES;

    @NotNull(message = "La hora de inicio del horario es obligatoria")
    private LocalTime horaInicio;

    @NotNull(message = "La hora de fin del horario es obligatoria")
    private LocalTime horaFin;
}
