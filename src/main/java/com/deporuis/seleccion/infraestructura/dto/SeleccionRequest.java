package com.deporuis.seleccion.infraestructura.dto;

import com.deporuis.seleccion.dominio.TipoSeleccion;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class SeleccionRequest {

    @NotNull(message = "La fecha de creación es obligatoria.")
    private LocalDate fechaCreacion;

    @NotNull(message = "El nombre de la selección es obligatorio.")
    private String nombreSeleccion;

    @NotNull(message = "El espacio deportivo es obligatorio.")
    private String espacioDeportivo;

    @NotNull(message = "El valor de equipo es obligatorio.")
    private Boolean equipo;

    @NotNull(message = "El id del deporte es obligatorio.")
    private Integer idDeporte;

    @NotNull(message = "El tipo de selección es obligatorio.")
    private TipoSeleccion tipo_seleccion;
}
