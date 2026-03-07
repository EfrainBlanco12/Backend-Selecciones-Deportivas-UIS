package com.deporuis.logro.infraestructura.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
public class LogroResponse {
    private Integer idLogro;

    private LocalDate fechaLogro;

    private String nombreLogro;

    private String descripcionLogro;

    private List<Integer> selecciones;
}
