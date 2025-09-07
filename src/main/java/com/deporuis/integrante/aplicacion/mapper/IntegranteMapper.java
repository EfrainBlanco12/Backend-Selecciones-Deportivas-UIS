package com.deporuis.integrante.aplicacion.mapper;

import com.deporuis.Foto.aplicacion.mapper.FotoMapper;
import com.deporuis.auth.infraestructura.dto.RolResponse;
import com.deporuis.integrante.dominio.Integrante;
import com.deporuis.integrante.infraestructura.dto.IntegranteRequest;
import com.deporuis.integrante.infraestructura.dto.IntegranteResponse;
import com.deporuis.posicion.infraestructura.dto.PosicionResponse;

import java.util.List;
import java.util.Objects;

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

    /** Alias para mantener compatibilidad con servicios existentes */
    public static IntegranteResponse integranteToResponse(Integrante integrante) {
        return toResponse(integrante);
    }

    /** Mapea Integrante -> IntegranteResponse con:
     *  - idSeleccion (solo id)
     *  - rol (objeto), foto (objeto), posiciones (lista de objetos)
     */
    public static IntegranteResponse toResponse(Integrante integrante) {
        if (integrante == null) return null;

        IntegranteResponse dto = new IntegranteResponse();

        dto.setIdIntegrante(integrante.getIdIntegrante());
        dto.setCodigoUniversitario(integrante.getCodigoUniversitario());
        dto.setNombres(integrante.getNombres());
        dto.setApellidos(integrante.getApellidos());
        dto.setFechaNacimiento(integrante.getFechaNacimiento());
        dto.setAltura(integrante.getAltura());
        dto.setPeso(integrante.getPeso());
        dto.setDorsal(integrante.getDorsal());
        dto.setCorreoUniversitario(integrante.getCorreoInstitucional());

        // Solo ID para selección
        dto.setIdSeleccion(
                integrante.getSeleccion() != null ? integrante.getSeleccion().getIdSeleccion() : null
        );

        // Objeto completo para rol
        dto.setRol(
                integrante.getRol() != null ? new RolResponse(integrante.getRol()) : null
        );

        // Objeto completo para foto
        dto.setFoto(
                integrante.getFoto() != null ? FotoMapper.toResponse(integrante.getFoto()) : null
        );

        // Lista de objetos para posiciones (NO IDs)
        List<PosicionResponse> posiciones = (integrante.getPosiciones() == null)
                ? List.of()
                : integrante.getPosiciones().stream()
                .map(ip -> ip != null ? ip.getPosicion() : null)
                .filter(Objects::nonNull)
                .map(PosicionResponse::new)
                .toList();
        dto.setPosiciones(posiciones);

        return dto;
    }
}
