package com.deporuis.logro.aplicacion.mapper;

import com.deporuis.logro.dominio.Logro;
import com.deporuis.logro.infraestructura.dto.LogroRequest;
import com.deporuis.logro.infraestructura.dto.LogroResponse;

import java.util.List;

public class LogroMapper {
    public static Logro requestToLogro(LogroRequest logroRequest) {
        return new Logro(
                logroRequest.getFechaLogro(),
                logroRequest.getNombreLogro(),
                logroRequest.getDescripcionLogro()
        );
    }

    public static LogroResponse toResponse(Logro logro) {
        List<Integer> selecciones = logro.getSelecciones().stream()
                .map(seleccionLogro -> seleccionLogro.getSeleccion().getIdSeleccion()).toList();
        return new LogroResponse(
                logro.getIdLogro(),
                logro.getFechaLogro(),
                logro.getNombreLogro(),
                logro.getDescripcionLogro(),
                selecciones
        );
    }
}
