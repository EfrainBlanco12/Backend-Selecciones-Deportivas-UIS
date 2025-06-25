package com.deporuis.seleccion.infraestructura.dto;

import com.deporuis.seleccion.dominio.TipoSeleccion;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
public class SeleccionResponse {
    private Integer idSeleccion;

    private LocalDate fechaCreacion;

    private String nombreSeleccion;

    private String espacioDeportivo;

    private Boolean equipo;

    private TipoSeleccion tipoSeleccion;

    private Integer deporte;

    private List<Integer> fotos;

    private List<Integer> horarios;
}


