package com.deporuis.integrante.aplicacion.mapper;

import com.deporuis.integrante.dominio.Integrante;
import com.deporuis.integrante.infraestructura.dto.IntegranteRequest;
import com.deporuis.integrante.infraestructura.dto.IntegranteResponse;

public class IntegranteMapper {
    public static Integrante requestToIntegrante(IntegranteRequest integranteRequest) {
        return new Integrante(
                integranteRequest.getCodigoUniversitario(),
                integranteRequest.getNombres(),
                integranteRequest.getApellidos(),
                integranteRequest.getFechaNacimiento(),
                integranteRequest.getAltura(),
                integranteRequest.getPeso(),
                integranteRequest.getDorsal(),
                integranteRequest.getCorreoInstitucional()
        );
    }

    public static IntegranteResponse integranteToResponse(Integrante integrante) {
        return new IntegranteResponse(
                integrante.getIdIntegrante(),
                integrante.getCodigoUniversitario(),
                integrante.getNombres(),
                integrante.getApellidos(),
                integrante.getFechaNacimiento(),
                integrante.getAltura(),
                integrante.getPeso(),
                integrante.getDorsal(),
                integrante.getCorreoInstitucional(),
                integrante.getRol().getIdRol(),
                integrante.getSeleccion().getIdSeleccion(),
                integrante.getFoto().getIdFoto(),
                integrante.getPosiciones().stream().map(integrantePosicion -> integrantePosicion.getPosicion().getIdPosicion()).toList()
        );
    }
}
