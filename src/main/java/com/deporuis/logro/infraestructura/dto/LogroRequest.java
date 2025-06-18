package com.deporuis.logro.infraestructura.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class LogroRequest {

    @NotNull(message = "La fecha del logro es obligatoria")
    private LocalDate fechaLogro;

    @NotBlank(message = "El nombre del logro es obligatoria")
    private String nombreLogro;

    private String descripcionLogro;

    @NotNull(message = "Debe elegir al menos una seleccion")
    private List<Integer> selecciones;
}
