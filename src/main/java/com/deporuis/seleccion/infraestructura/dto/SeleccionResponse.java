package com.deporuis.seleccion.infraestructura.dto;

import com.deporuis.seleccion.dominio.TipoSeleccion;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class SeleccionResponse {
    private Integer idSeleccion;
    private String nombreSeleccion;

}


