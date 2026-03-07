package com.deporuis.seleccion.infraestructura.dto;

import com.deporuis.Foto.infraestructura.dto.FotoRequest;
import com.deporuis.horario.infraestructura.dto.HorarioRequest;
import com.deporuis.seleccion.dominio.TipoSeleccion;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class SeleccionRequest {

    @NotNull(message = "La fecha de creación es obligatoria.")
    private LocalDate fechaCreacion;

    @NotBlank(message = "El nombre de la selección es obligatorio.")
    private String nombreSeleccion;

    @NotBlank(message = "El espacio deportivo es obligatorio.")
    private String espacioDeportivo;

    @NotNull(message = "El valor de equipo es obligatorio.")
    private Boolean equipo;

    @NotNull(message = "El tipo de selección es obligatorio.")
    private TipoSeleccion tipo_seleccion;

    @NotNull(message = "El id del deporte es obligatorio.")
    private Integer idDeporte;

    @NotNull(message = "Debe elegir al menos una foto")
    private List<FotoRequest> fotos;

    @NotNull(message = "Debe elegir al menos un horario")
    private List<HorarioRequest> horarios;
}
