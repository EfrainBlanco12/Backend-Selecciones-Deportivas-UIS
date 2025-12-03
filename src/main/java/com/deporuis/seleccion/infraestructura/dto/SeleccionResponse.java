package com.deporuis.seleccion.infraestructura.dto;

import com.deporuis.Foto.infraestructura.dto.FotoResponse;
import com.deporuis.deporte.infraestructura.dto.DeporteResponse;
import com.deporuis.horario.infraestructura.dto.HorarioResponse;
import com.deporuis.seleccion.dominio.TipoSeleccion;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeleccionResponse {
    private Integer idSeleccion;

    private LocalDate fechaCreacion;

    private String nombreSeleccion;

    private String espacioDeportivo;

    private Boolean equipo;

    private TipoSeleccion tipoSeleccion;

    private DeporteResponse deporte;

    private List<FotoResponse> fotos;

    private List<HorarioResponse> horarios;

    private Integer usuarioModifico;

    private LocalDateTime fechaModificacion;
}
