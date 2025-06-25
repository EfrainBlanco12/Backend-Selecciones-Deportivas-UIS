package com.deporuis.seleccion.aplicacion.mapper;

import com.deporuis.Foto.dominio.Foto;
import com.deporuis.seleccion.dominio.Seleccion;
import com.deporuis.seleccion.infraestructura.dto.SeleccionRequest;
import com.deporuis.seleccion.infraestructura.dto.SeleccionResponse;

public class SeleccionMapper {
    public static Seleccion requestToSeleccion(SeleccionRequest request) {
        return new Seleccion(
                request.getFechaCreacion(),
                request.getNombreSeleccion(),
                request.getEspacioDeportivo(),
                request.getEquipo(),
                request.getTipo_seleccion()
        );
    }

    public static SeleccionResponse seleccionToResponse(Seleccion seleccion) {
        return new SeleccionResponse(
                seleccion.getIdSeleccion(),
                seleccion.getFechaCreacion(),
                seleccion.getNombreSeleccion(),
                seleccion.getEspacioDeportivo(),
                seleccion.getEquipo(),
                seleccion.getTipo_seleccion(),
                seleccion.getDeporte().getIdDeporte(),
                seleccion.getFotos().stream().map(Foto::getIdFoto).toList(),
                seleccion.getHorarios().stream().map(seleccionHorario -> seleccionHorario.getHorario().getIdHorario()).toList()
        );
    }
}
