package com.deporuis.seleccion.infraestructura.dto;

import com.deporuis.Foto.infraestructura.dto.FotoRequest;
import com.deporuis.horario.infraestructura.dto.HorarioRequest;
import com.deporuis.seleccion.dominio.TipoSeleccion;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class SeleccionPatchRequest {

    private LocalDate fechaCreacion;

    private String nombreSeleccion;

    private String espacioDeportivo;

    private Boolean equipo;

    private TipoSeleccion tipo_seleccion;

    private Integer idDeporte;

    private List<FotoRequest> fotos;

    private List<HorarioRequest> horarios;
}
