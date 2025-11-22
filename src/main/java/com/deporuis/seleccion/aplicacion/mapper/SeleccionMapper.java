package com.deporuis.seleccion.aplicacion.mapper;

import com.deporuis.Foto.infraestructura.dto.FotoResponse;
import com.deporuis.deporte.infraestructura.dto.DeporteResponse;
import com.deporuis.horario.infraestructura.dto.HorarioResponse;
import com.deporuis.seleccion.dominio.Seleccion;
import com.deporuis.seleccion.infraestructura.dto.SeleccionRequest;
import com.deporuis.seleccion.infraestructura.dto.SeleccionResponse;

import java.util.List;

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
        DeporteResponse deporteDto = null;
        if (seleccion.getDeporte() != null) {
            deporteDto = new DeporteResponse(seleccion.getDeporte());
        }

        List<FotoResponse> fotosDto = seleccion.getFotos() == null
                ? List.of()
                : seleccion.getFotos().stream()
                .map(f -> new FotoResponse(
                        f.getIdFoto(),
                        f.getContenido(),
                        f.getTemporada(),
                        f.getIntegrante() != null ? f.getIntegrante().getIdIntegrante() : null,
                        f.getSeleccion() != null ? f.getSeleccion().getIdSeleccion() : null,
                        f.getPublicacion() != null ? f.getPublicacion().getIdPublicacion() : null
                ))
                .toList();

        List<HorarioResponse> horariosDto = seleccion.getHorarios() == null
                ? List.of()
                : seleccion.getHorarios().stream()
                .map(sh -> {
                    var h = sh.getHorario();
                    return new HorarioResponse(
                            h.getIdHorario(),
                            h.getDia(),
                            h.getHoraInicio(),
                            h.getHoraFin(),
                            // Mantengo solo el id de esta selección para no inflar la respuesta
                            List.of(seleccion.getIdSeleccion())
                    );
                })
                .toList();

        return new SeleccionResponse(
                seleccion.getIdSeleccion(),
                seleccion.getFechaCreacion(),
                seleccion.getNombreSeleccion(),
                seleccion.getEspacioDeportivo(),
                seleccion.getEquipo(),
                seleccion.getTipo_seleccion(),
                deporteDto,
                fotosDto,
                horariosDto
        );
    }
}
