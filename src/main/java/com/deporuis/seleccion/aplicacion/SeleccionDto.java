package com.deporuis.seleccion.aplicacion;

import lombok.Data;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Data
public class SeleccionDto {

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

}
