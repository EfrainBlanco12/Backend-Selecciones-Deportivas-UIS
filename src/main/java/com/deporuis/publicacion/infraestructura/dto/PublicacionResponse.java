package com.deporuis.publicacion.infraestructura.dto;

import com.deporuis.Foto.infraestructura.dto.FotoResponse;
import com.deporuis.publicacion.dominio.TipoPublicacion;
import com.deporuis.seleccion.infraestructura.dto.SeleccionPublicacionResponse;
import com.deporuis.seleccion.infraestructura.dto.SeleccionResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PublicacionResponse {

    private Integer idPublicacion;
    private String titulo;
    private String descripcion;
    private String lugar;
    private LocalDateTime fecha;
    private String duracion;
    private Boolean visibilidad;
    private String tipoPublicacion;
    private LocalDateTime fechaCreacion;

    private List<FotoResponse> fotos;
    private List<SeleccionPublicacionResponse> idSelecciones;
}
